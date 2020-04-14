package parser;

public class Instruction {
    String mnemonic;
    Byte opcode = null;
    Byte f3 = null;
    Byte f7 = null;
    Byte rd = null;
    Byte rs1 = null;
    Byte rs2 = null;
    Byte imm = null;

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
