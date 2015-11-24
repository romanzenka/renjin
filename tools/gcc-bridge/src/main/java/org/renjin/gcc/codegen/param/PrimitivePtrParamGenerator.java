package org.renjin.gcc.codegen.param;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.renjin.gcc.codegen.LocalVarAllocator;
import org.renjin.gcc.codegen.WrapperType;
import org.renjin.gcc.codegen.expr.ExprGenerator;
import org.renjin.gcc.codegen.var.PrimitivePtrVarGenerator;
import org.renjin.gcc.gimple.type.GimpleIndirectType;
import org.renjin.gcc.gimple.type.GimpleType;
import org.renjin.gcc.runtime.Ptr;

import java.util.Collections;
import java.util.List;

import static org.objectweb.asm.Opcodes.*;


/**
 * Parameter that is a pointer (e.g. {@code double*} and is mapped to a wrapped
 * Pointer type, such as DoublePtr or ObjectPtr.
 */
public class PrimitivePtrParamGenerator extends ParamGenerator {

  private final GimpleIndirectType type;

  /**
   * The {@link Ptr} subclass type
   */
  private final WrapperType pointerType;
  

  public PrimitivePtrParamGenerator(GimpleType type) {
    this.type = (GimpleIndirectType) type;
    this.pointerType = WrapperType.forPointerType(this.type);
  }

  @Override
  public GimpleType getGimpleType() {
    return type;
  }

  @Override
  public List<Type> getParameterTypes() {
    return Collections.singletonList(pointerType.getWrapperType());
  }

  @Override
  public ExprGenerator emitInitialization(MethodVisitor mv, int localVariableIndex, LocalVarAllocator localVars) {
    
    // Unpack the wrapper into seperate array and offset fields
    int arrayVariable = localVars.reserve(1);
    int offsetVariable = localVars.reserve(1);
    
    // Load the parameter on the stack
    mv.visitVarInsn(ALOAD, localVariableIndex);
    
    // duplicate the wrapper instance so we can call GETFIELD twice.
    mv.visitInsn(DUP);

    // Consume the first reference to the wrapper type and push the array field on the stack
    mv.visitFieldInsn(GETFIELD, pointerType.getWrapperType().getInternalName(), "array", pointerType.getArrayType().getDescriptor());

    // Store the array reference in the local variable
    mv.visitVarInsn(ASTORE, arrayVariable);
    
    // Consume the second reference 
    mv.visitFieldInsn(GETFIELD, pointerType.getWrapperType().getInternalName(), "offset", "I");

    // Store the array reference in the local variable
    mv.visitVarInsn(ISTORE, offsetVariable);
    
    return new PrimitivePtrVarGenerator(type, arrayVariable, offsetVariable);
  }

  @Override
  public void emitPushParameter(MethodVisitor mv, ExprGenerator parameterValueGenerator) {
    parameterValueGenerator.emitPushPointerWrapper(mv);
  }
  
}