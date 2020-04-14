import parser.ELFParser;
import parser.ParsedELFBinary;

import java.io.IOException;

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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
