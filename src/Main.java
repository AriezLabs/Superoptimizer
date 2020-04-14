import parser.ELFParser;
import parser.Instruction;
import parser.ParsedELFBinary;
import parser.Util;

import java.io.*;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("ELF binary path must be the sole argument.");
            System.exit(1);
        }
        try {
            ParsedELFBinary binary = ELFParser.parse(args[0]);
            System.out.println("Number of instructions: " + binary.instructions.length);
            System.out.println("Length of code segment: " + binary.codeSegment.length);
            System.out.println("Length of data segment: " + binary.dataSegment.length);
            System.out.println("Entry point: 0x" + Long.toHexString(binary.entryPoint));
            File output = new File("selfie.s");
            PrintWriter pw = new PrintWriter(output);

            for (Instruction instruction : binary.instructions) {
                pw.println(instruction);
            }
            for (int i = 0; i < binary.dataSegment.length; i += 8) {
                byte[] insBytes = Arrays.copyOfRange(binary.dataSegment, i, i + 8);
                long data = Util.byteArrayToLeLong(insBytes);
                pw.println(String.format(".quad 0x%X", data));
            }

            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
