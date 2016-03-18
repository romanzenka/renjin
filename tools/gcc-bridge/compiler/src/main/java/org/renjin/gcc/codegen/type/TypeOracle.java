package org.renjin.gcc.codegen.type;

import com.google.common.collect.Maps;
import org.objectweb.asm.Type;
import org.renjin.gcc.InternalCompilerException;
import org.renjin.gcc.codegen.RecordClassGenerator;
import org.renjin.gcc.codegen.WrapperType;
import org.renjin.gcc.codegen.type.complex.ComplexTypeStrategy;
import org.renjin.gcc.codegen.type.fun.FunTypeStrategy;
import org.renjin.gcc.codegen.type.primitive.*;
import org.renjin.gcc.codegen.type.record.RecordFatPtrParamStrategy;
import org.renjin.gcc.codegen.type.record.RecordTypeStrategy;
import org.renjin.gcc.codegen.type.record.RecordUnitPtrReturnStrategy;
import org.renjin.gcc.codegen.type.voidt.VoidReturnStrategy;
import org.renjin.gcc.codegen.type.voidt.VoidTypeStrategy;
import org.renjin.gcc.gimple.GimpleParameter;
import org.renjin.gcc.gimple.type.*;
import org.renjin.gcc.runtime.BytePtr;
import org.renjin.gcc.runtime.CharPtr;
import org.renjin.gcc.runtime.ObjectPtr;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Provides the {@link TypeStrategy} for each {@link GimpleType}
 * 
 * <p>There is a single instance of the {@code TypeOracle} for each compilation, which
 * might compile several compilation units simultaneously. The {@code TypeOracle} holds all information about 
 * Gimple types at compile time, and so can provide the right strategy for code generation.</p>
 */
public class TypeOracle {

  private final Map<String, RecordClassGenerator> recordTypes = Maps.newHashMap();

  /**
   * Map from internal JVM class name to a GimpleType
   */
  private final Map<String, GimpleRecordType> classTypes = Maps.newHashMap();

  public void addRecordType(GimpleRecordTypeDef type, RecordClassGenerator generator) {
    recordTypes.put(type.getId(), generator);
    classTypes.put(generator.getClassName(), generator.getGimpleType());
  }
  
  public Type typeForRecord(GimpleRecordType type) {
    RecordClassGenerator recordClassGenerator = recordTypes.get(type.getId());
    if(recordClassGenerator == null) {
      throw new InternalCompilerException("No such record type: " + type);
    }
    return recordClassGenerator.getType();
  }
  
  public TypeStrategy forType(GimpleType type) {
    if(type instanceof GimplePrimitiveType) {
      return new PrimitiveTypeStrategy((GimplePrimitiveType) type);

    } else if(type instanceof GimpleComplexType) {
      return new ComplexTypeStrategy((GimpleComplexType) type);
    
    } else if(type instanceof GimpleFunctionType) {
      return new FunTypeStrategy((GimpleFunctionType) type);

    } else if(type instanceof GimpleVoidType) {
      return new VoidTypeStrategy();
      
    } else if (type instanceof GimpleRecordType) {
      GimpleRecordType recordType = (GimpleRecordType) type;
      RecordClassGenerator recordGenerator = recordTypes.get(recordType.getId());
      if(recordGenerator == null) {
        throw new InternalCompilerException(String.format(
            "No record type for GimpleRecordType[name: %s, id: %s]", recordType.getName(), recordType.getId()));
      }
      return new RecordTypeStrategy(recordGenerator);
      
    } else if(type instanceof GimpleIndirectType) {
      return forType(type.getBaseType()).pointerTo();
    
    } else if(type instanceof GimpleArrayType) {
      GimpleArrayType arrayType = (GimpleArrayType) type;
      return forType(arrayType.getComponentType()).arrayOf(arrayType);
    
    } else {
      throw new UnsupportedOperationException("Unsupported type: " + type);
    }
  }
  

  public ParamStrategy forParameter(GimpleType parameterType) {
    return forType(parameterType).getParamStrategy();
  }

  /**
   * Creates a new FieldGenerator for a given field type.
   * 
   * @param className the full internal name of the class in which the field is declared (for example, "org/renjin/gcc/Struct")
   * @param field the gimple field
   */
  public FieldGenerator forField(String className, GimpleField field) {
    TypeStrategy type = forType(field.getType());
    if(field.isAddressed()) {
      return type.addressableFieldGenerator(className, field.getName());
    } else {
      return type.fieldGenerator(className, field.getName());
    }
  }

  public ReturnStrategy findReturnGenerator(GimpleType returnType) {
    return forType(returnType).getReturnStrategy();
  }
  
  public ReturnStrategy forReturnValue(Method method) {
    Class<?> returnType = method.getReturnType();
    if(returnType.equals(void.class)) {
      return new VoidReturnStrategy();

    } else if(returnType.isPrimitive()) {
      return new PrimitiveReturnStrategy(GimplePrimitiveType.fromJvmType(returnType));

    } else if(WrapperType.is(returnType)) {
      WrapperType wrapperType = WrapperType.valueOf(returnType);
      if(wrapperType.equals(WrapperType.OBJECT_PTR)) {
        // Signature should be in the form ObjectPtr<BaseT>
        // Use generics to get the base type 
        java.lang.reflect.Type genericReturnType = method.getGenericReturnType();
        Class baseType = objectPtrBaseType(genericReturnType);
        
        if(baseType.equals(ObjectPtr.class)) {
          throw new UnsupportedOperationException(genericReturnType.toString());
        } else if(WrapperType.is(baseType)) {
          WrapperType innerWrapper = WrapperType.valueOf(baseType);
          GimplePointerType pointerPointerType = innerWrapper.getGimpleType().pointerTo();
          return new PrimitivePtrPtrReturnStrategy(pointerPointerType);
        } else {
          throw new UnsupportedOperationException("baseType: " + baseType);
        }
      } else {
        return new PrimitivePtrReturnStrategy(wrapperType.getGimpleType());
      }
    } else if(classTypes.containsKey(Type.getInternalName(returnType))) {
      GimpleRecordType recordType = classTypes.get(Type.getInternalName(returnType));
      return new RecordUnitPtrReturnStrategy(recordTypes.get(recordType.getId()));

    } else {
      throw new UnsupportedOperationException(String.format(
          "Unsupported return type %s in method %s.%s",
          returnType.getName(),
          method.getDeclaringClass().getName(), method.getName()));
    }
  }
  
  public List<ParamStrategy> forParameterTypes(List<GimpleType> parameterTypes) {
    List<ParamStrategy> generators = new ArrayList<ParamStrategy>();
    for (GimpleType parameterType : parameterTypes) {
      ParamStrategy param = forParameter(parameterType);
      generators.add(param);
    }
    return generators;
  }


  /**
   * Creates a list of {@code ParamGenerators} from an existing JVM method.
   *
   * <p>Note that there is not a one-to-one relationship between JVM method parameters and
   * our {@code ParamGenerators}; a complex pointer is represented as a {@code double[]} and an 
   * {@code int} offset, for example.</p>
   */
  public List<ParamStrategy> forParameterTypesOf(Method method) {

    List<ParamStrategy> generators = new ArrayList<ParamStrategy>();

    int numParams;
    if(method.isVarArgs()) {
      numParams = method.getParameterTypes().length - 1;
    } else {
      numParams = method.getParameterTypes().length;
    }
    
    int index = 0;
    while(index < numParams) {
      Class<?> paramClass = method.getParameterTypes()[index];
      if(paramClass.equals(ObjectPtr.class)) {
        generators.add(forObjectPtrParam(method.getGenericParameterTypes()[index]));
        index++;
        
      } else if (WrapperType.is(paramClass) && !paramClass.equals(CharPtr.class)) {
        WrapperType wrapperType = WrapperType.valueOf(paramClass);
        generators.add(new PrimitivePtrParamStrategy(wrapperType.getGimpleType()));
        index++;

      } else if (paramClass.isPrimitive()) {
        generators.add(new PrimitiveParamStrategy(GimplePrimitiveType.fromJvmType(paramClass)));
        index++;

      } else if (paramClass.equals(String.class)) {
        generators.add(new StringParamStrategy());
        index++;

      } else if (classTypes.containsKey(Type.getInternalName(paramClass))) {
        GimpleRecordType mappedType = classTypes.get(Type.getInternalName(paramClass));
        generators.add(forType(mappedType).pointerTo().getParamStrategy());
        index++;
        
      } else {
        throw new UnsupportedOperationException(String.format(
            "Unsupported parameter %d of type %s", 
            index,
            paramClass.getName()));
      } 
    }
    return generators;
  }
  
  private Class objectPtrBaseType(java.lang.reflect.Type type) {
    if (!(type instanceof ParameterizedType)) {
      throw new InternalCompilerException(ObjectPtr.class.getSimpleName() + " parameters must be parameterized");
    }
    ParameterizedType parameterizedType = (ParameterizedType) type;
    return (Class) parameterizedType.getActualTypeArguments()[0];
  }
  
  private ParamStrategy forObjectPtrParam(java.lang.reflect.Type type) {
    Class baseType = objectPtrBaseType(type);
    if(baseType.equals(BytePtr.class)) {
      return forType(new GimpleIntegerType(8)).pointerTo().pointerTo().getParamStrategy();
    } else {
      String baseTypeInternalName = Type.getInternalName((Class)baseType);
      if(classTypes.containsKey(baseTypeInternalName)) {
        GimpleRecordType mappedType = classTypes.get(baseTypeInternalName);
        RecordClassGenerator recordClassGenerator = recordTypes.get(mappedType.getId());
        return new RecordFatPtrParamStrategy(recordClassGenerator);
      }
    }
    throw new UnsupportedOperationException("TODO: baseType = " + baseType);
  }

  public Map<GimpleParameter, ParamStrategy> forParameters(List<GimpleParameter> parameters) {
    Map<GimpleParameter, ParamStrategy> map = new HashMap<GimpleParameter, ParamStrategy>();
    for (GimpleParameter parameter : parameters) {
      map.put(parameter, forParameter(parameter.getType()));
    }
    return map;
  }
}