package io.xserverless.function.command;

public class OpcodeDecode {
    public static final String CODES[] = new String[255];

    static {
        CODES[0] = "NOP"; // visitInsn
        CODES[1] = "ACONST_NULL"; // -
        CODES[2] = "ICONST_M1"; // -
        CODES[3] = "ICONST_0"; // -
        CODES[4] = "ICONST_1"; // -
        CODES[5] = "ICONST_2"; // -
        CODES[6] = "ICONST_3"; // -
        CODES[7] = "ICONST_4"; // -
        CODES[8] = "ICONST_5"; // -
        CODES[9] = "LCONST_0"; // -
        CODES[10] = "LCONST_1"; // -
        CODES[11] = "FCONST_0"; // -
        CODES[12] = "FCONST_1"; // -
        CODES[13] = "FCONST_2"; // -
        CODES[14] = "DCONST_0"; // -
        CODES[15] = "DCONST_1"; // -
        CODES[16] = "BIPUSH"; // visitIntInsn
        CODES[17] = "SIPUSH"; // -
        CODES[18] = "LDC"; // visitLdcInsn
        CODES[21] = "ILOAD"; // visitVarInsn
        CODES[22] = "LLOAD"; // -
        CODES[23] = "FLOAD"; // -
        CODES[24] = "DLOAD"; // -
        CODES[25] = "ALOAD"; // -
        CODES[46] = "IALOAD"; // visitInsn
        CODES[47] = "LALOAD"; // -
        CODES[48] = "FALOAD"; // -
        CODES[49] = "DALOAD"; // -
        CODES[50] = "AALOAD"; // -
        CODES[51] = "BALOAD"; // -
        CODES[52] = "CALOAD"; // -
        CODES[53] = "SALOAD"; // -
        CODES[54] = "ISTORE"; // visitVarInsn
        CODES[55] = "LSTORE"; // -
        CODES[56] = "FSTORE"; // -
        CODES[57] = "DSTORE"; // -
        CODES[58] = "ASTORE"; // -
        CODES[79] = "IASTORE"; // visitInsn
        CODES[80] = "LASTORE"; // -
        CODES[81] = "FASTORE"; // -
        CODES[82] = "DASTORE"; // -
        CODES[83] = "AASTORE"; // -
        CODES[84] = "BASTORE"; // -
        CODES[85] = "CASTORE"; // -
        CODES[86] = "SASTORE"; // -
        CODES[87] = "POP"; // -
        CODES[88] = "POP2"; // -
        CODES[89] = "DUP"; // -
        CODES[90] = "DUP_X1"; // -
        CODES[91] = "DUP_X2"; // -
        CODES[92] = "DUP2"; // -
        CODES[93] = "DUP2_X1"; // -
        CODES[94] = "DUP2_X2"; // -
        CODES[95] = "SWAP"; // -
        CODES[96] = "IADD"; // -
        CODES[97] = "LADD"; // -
        CODES[98] = "FADD"; // -
        CODES[99] = "DADD"; // -
        CODES[100] = "ISUB"; // -
        CODES[101] = "LSUB"; // -
        CODES[102] = "FSUB"; // -
        CODES[103] = "DSUB"; // -
        CODES[104] = "IMUL"; // -
        CODES[105] = "LMUL"; // -
        CODES[106] = "FMUL"; // -
        CODES[107] = "DMUL"; // -
        CODES[108] = "IDIV"; // -
        CODES[109] = "LDIV"; // -
        CODES[110] = "FDIV"; // -
        CODES[111] = "DDIV"; // -
        CODES[112] = "IREM"; // -
        CODES[113] = "LREM"; // -
        CODES[114] = "FREM"; // -
        CODES[115] = "DREM"; // -
        CODES[116] = "INEG"; // -
        CODES[117] = "LNEG"; // -
        CODES[118] = "FNEG"; // -
        CODES[119] = "DNEG"; // -
        CODES[120] = "ISHL"; // -
        CODES[121] = "LSHL"; // -
        CODES[122] = "ISHR"; // -
        CODES[123] = "LSHR"; // -
        CODES[124] = "IUSHR"; // -
        CODES[125] = "LUSHR"; // -
        CODES[126] = "IAND"; // -
        CODES[127] = "LAND"; // -
        CODES[128] = "IOR"; // -
        CODES[129] = "LOR"; // -
        CODES[130] = "IXOR"; // -
        CODES[131] = "LXOR"; // -
        CODES[132] = "IINC"; // visitIincInsn
        CODES[133] = "I2L"; // visitInsn
        CODES[134] = "I2F"; // -
        CODES[135] = "I2D"; // -
        CODES[136] = "L2I"; // -
        CODES[137] = "L2F"; // -
        CODES[138] = "L2D"; // -
        CODES[139] = "F2I"; // -
        CODES[140] = "F2L"; // -
        CODES[141] = "F2D"; // -
        CODES[142] = "D2I"; // -
        CODES[143] = "D2L"; // -
        CODES[144] = "D2F"; // -
        CODES[145] = "I2B"; // -
        CODES[146] = "I2C"; // -
        CODES[147] = "I2S"; // -
        CODES[148] = "LCMP"; // -
        CODES[149] = "FCMPL"; // -
        CODES[150] = "FCMPG"; // -
        CODES[151] = "DCMPL"; // -
        CODES[152] = "DCMPG"; // -
        CODES[153] = "IFEQ"; // visitJumpInsn
        CODES[154] = "IFNE"; // -
        CODES[155] = "IFLT"; // -
        CODES[156] = "IFGE"; // -
        CODES[157] = "IFGT"; // -
        CODES[158] = "IFLE"; // -
        CODES[159] = "IF_ICMPEQ"; // -
        CODES[160] = "IF_ICMPNE"; // -
        CODES[161] = "IF_ICMPLT"; // -
        CODES[162] = "IF_ICMPGE"; // -
        CODES[163] = "IF_ICMPGT"; // -
        CODES[164] = "IF_ICMPLE"; // -
        CODES[165] = "IF_ACMPEQ"; // -
        CODES[166] = "IF_ACMPNE"; // -
        CODES[167] = "GOTO"; // -
        CODES[168] = "JSR"; // -
        CODES[169] = "RET"; // visitVarInsn
        CODES[170] = "TABLESWITCH"; // visiTableSwitchInsn
        CODES[171] = "LOOKUPSWITCH"; // visitLookupSwitch
        CODES[172] = "IRETURN"; // visitInsn
        CODES[173] = "LRETURN"; // -
        CODES[174] = "FRETURN"; // -
        CODES[175] = "DRETURN"; // -
        CODES[176] = "ARETURN"; // -
        CODES[177] = "RETURN"; // -
        CODES[178] = "GETSTATIC"; // visitFieldInsn
        CODES[179] = "PUTSTATIC"; // -
        CODES[180] = "GETFIELD"; // -
        CODES[181] = "PUTFIELD"; // -
        CODES[182] = "INVOKEVIRTUAL"; // visitMethodInsn
        CODES[183] = "INVOKESPECIAL"; // -
        CODES[184] = "INVOKESTATIC"; // -
        CODES[185] = "INVOKEINTERFACE"; // -
        CODES[186] = "INVOKEDYNAMIC"; // visitInvokeDynamicInsn
        CODES[187] = "NEW"; // visitTypeInsn
        CODES[188] = "NEWARRAY"; // visitIntInsn
        CODES[189] = "ANEWARRAY"; // visitTypeInsn
        CODES[190] = "ARRAYLENGTH"; // visitInsn
        CODES[191] = "ATHROW"; // -
        CODES[192] = "CHECKCAST"; // visitTypeInsn
        CODES[193] = "INSTANCEOF"; // -
        CODES[194] = "MONITORENTER"; // visitInsn
        CODES[195] = "MONITOREXIT"; // -
        CODES[197] = "MULTIANEWARRAY"; // visitMultiANewArrayInsn
        CODES[198] = "IFNULL"; // visitJumpInsn
        CODES[199] = "IFNONNULL"; // -
    }
}