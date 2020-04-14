package parser;

/**
 * Represents a concrete instruction with registers and immediate
 */
public class Instruction extends InstructionType {
    Byte rd = null;
    Byte rs1 = null;
    Byte rs2 = null;
    Byte imm = null;

    public Instruction(InstructionType type, int binary) {
        super(type.mnemonic, type.type, type.opcode, type.f3, type.f7);
        // TODO parse rd, rs1, rs1, imm depending on type
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();

        if (mnemonic != null)
            sb.append(mnemonic).append(" ");
        if (rd != null)
            sb.append(rd).append(" ");
        if (rs1 != null)
            sb.append(rs1).append(" ");
        if (rs2 != null)
            sb.append(rs2).append(" ");
        if (imm != null)
            sb.append(String.format("0x%8X "));

        return sb.toString();
    }
}
