package parser;

import static parser.Util.signExtend;

/**
 * Represents a concrete instruction with registers and immediate
 */
public class Instruction extends InstructionType {
    Byte rd = null;
    Byte rs1 = null;
    Byte rs2 = null;
    Integer imm = null;

    private static String[] regs = {"zero", "ra", "sp", "gp", "tp", "t0", "t1", "t2", "s0", "s1", "a0", "a1", "a2", "a3",
            "a4", "a5", "a6", "a7", "s2", "s3", "s4", "s5", "s6", "s7", "s8", "s9", "s10", "s11", "t3", "t4", "t5", "t6"};

    public Instruction(InstructionType type, int binary) {
        super(type.mnemonic, type.format, type.opcode, type.f3, type.f7);
        switch (type.format) {
            case R: {
                rd = (byte) extractBits(binary, 7, 11);
                rs1 = (byte) extractBits(binary, 15, 19);
                rs2 = (byte) extractBits(binary, 20, 24);
                break;
            }
            case I: {
                rd = (byte) extractBits(binary, 7, 11);
                rs1 = (byte) extractBits(binary, 15, 19);
                imm = extractBits(binary, 20, 31);
                imm = signExtend(imm, 12);
                break;
            }
            case S: {
                rs1 = (byte) extractBits(binary, 15, 19);
                rs2 = (byte) extractBits(binary, 20, 24);
                imm = extractBits(binary, 25, 31) << 5 | extractBits(binary, 7, 11);
                imm = signExtend(imm, 12);
                break;
            }
            case B: {
                rs1 = (byte) extractBits(binary, 15, 19);
                rs2 = (byte) extractBits(binary, 20, 24);
                imm = extractBits(binary, 8, 11) << 1 |
                        extractBits(binary, 25, 30) << 5 |
                        extractBits(binary, 7, 7) << 11 |
                        extractBits(binary, 31, 31) << 12;
                imm = signExtend(imm, 13);
                break;
            }
            case J: {
                rd = (byte) extractBits(binary, 7, 11);
                imm = extractBits(binary, 21, 30) << 1 |
                        extractBits(binary, 20, 20) << 11 |
                        extractBits(binary, 12, 19) << 12 |
                        extractBits(binary, 31, 31) << 20;
                imm = signExtend(imm, 21);
                break;
            }
            case U: {
                rd = (byte) extractBits(binary, 7, 11);
                imm = extractBits(binary, 12, 31);
                imm = signExtend(imm, 20);
                break;
            }
        }
    }

    public String toString() {
        switch(mnemonic) {
            case LUI: {
                return String.format("lui %s,0x%X", regs[rd], extractBits(imm, 0, 19));
            }
            case ADDI: {
                if (rd == 0 && rs1 == 0 && imm == 0) {
                    return "nop";
                }
                return String.format("addi %s,%s,%d", regs[rd], regs[rs1], imm);
            }
            case ADD:
            case SUB:
            case MUL:
            case DIVU:
            case REMU:
            case SLTU: {
                return String.format("%s %s,%s,%s", mnemonic.name().toLowerCase(), regs[rd], regs[rs1], regs[rs2]);
            }
            case LD: {
                return String.format("ld %s,%d(%s)", regs[rd], imm, regs[rs1]);
            }
            case SD: {
                return String.format("sd %s,%d(%s)", regs[rs2], imm, regs[rs1]);
            }
            case BEQ: {
                return String.format("beq %s,%s,%d", regs[rs1], regs[rs2], imm / 4);
            }
            case JAL: {
                return String.format("jal %s,%d", regs[rd], imm / 4);
            }
            case JALR: {
                return String.format("jalr %s,%d(%s)", regs[rd], imm / 4, regs[rs1]);
            }
            case ECALL: {
                return "ecall";
            }
            default: {
                throw new RuntimeException("Cannot convert " + mnemonic.name()  + " to String yet.");
            }
        }
    }
}
