package parser;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.util.Arrays;

public class ELFParser {
    // page-aligned ELF header for storing file header (64 bytes),
    // program header (56 bytes), and code length (8 bytes)
    private static final int ELF_HEADER_LEN = 4096;

    private static final InstructionType[] decodeTable = {
            // f3/f7 are null iff this type of instruction does not have a f3/f7 code
            new InstructionType(Mnemonic.ECALL, Format.I, 0b1110011, null, null),
            new InstructionType(Mnemonic.LD, Format.I, 0b0000011, 0b011, null),
            new InstructionType(Mnemonic.SD, Format.S, 0b0100011, 0b011, null),
            new InstructionType(Mnemonic.MUL, Format.R, 0b0110011, 0b000, 0b0000001),
            new InstructionType(Mnemonic.DIVU, Format.R, 0b0110011, 0b101, 0b0000001),
            new InstructionType(Mnemonic.REMU, Format.R, 0b0110011, 0b111, 0b0000001),
            new InstructionType(Mnemonic.SLTU, Format.R, 0b0110011, 0b011, 0b0000000),
            new InstructionType(Mnemonic.JALR, Format.I, 0b1100111, 0b000, null),
            new InstructionType(Mnemonic.LUI, Format.U, 0b0110111, null, null),
            new InstructionType(Mnemonic.ADDI, Format.I, 0b0010011, 0b000, null),
            new InstructionType(Mnemonic.SUB, Format.R, 0b0110011, 0b000, 0b0100000),
            new InstructionType(Mnemonic.ADD, Format.R, 0b0110011, 0b000, 0b0000000),
            new InstructionType(Mnemonic.BEQ, Format.B, 0b1100011, 0b000, null),
            new InstructionType(Mnemonic.JAL, Format.J, 0b1101111, null, null),
    };

    private static Instruction decode(int binary) {
        for (InstructionType type : decodeTable)
            if (type.matches(binary))
                return new Instruction(type, binary);

        throw new RuntimeException(String.format("unknown instruction 0x%08X", binary));
    }

    public static ParsedELFBinary parse(String path) throws IOException {
        try (RandomAccessFile file = new RandomAccessFile(path, "r")) {
            FileChannel inChannel = file.getChannel();
            long fileSize = inChannel.size();
            ByteBuffer buffer = ByteBuffer.allocate((int) fileSize);
            buffer.order(ByteOrder.LITTLE_ENDIAN);
            inChannel.read(buffer);
            buffer.rewind();

            byte[] magic = new byte[4];
            buffer.get(magic, 0, 4);
            if (magic[0] != 0x7f || magic[1] != 'E' || magic[2] != 'L' || magic[3] != 'F') {
                throw new RuntimeException("File does not have ELF magic");
            }

            long entry_point = buffer.getLong(10 * 8);
            long binary_length = buffer.getLong(12 * 8);
            long code_length = buffer.getLong(15 * 8);

            // TODO: validate rest of header

            buffer.position(ELF_HEADER_LEN);
            byte[] code = new byte[(int) code_length];
            buffer.get(code, 0, code.length);
            byte[] data = new byte[(int) (binary_length - code_length)];
            buffer.get(data, 0, data.length);

            if (buffer.hasRemaining()) {
                throw new RuntimeException("Did not reach EOF after reading code and data segment. Corrupted file?");
            }

            Instruction[] instructions = new Instruction[code.length / 4];
            for (int i = 0; i < code.length; i += 4) {
                // System.out.println("Decoding instruction at " + String.format("0x%x", i) + "...");
                byte[] insBytes = Arrays.copyOfRange(code, i, i + 4);
                int insInt = Util.byteArrayToLeInt(insBytes);
                instructions[i / 4] = decode(insInt);
                // System.out.println("Got " + instructions[i / 4].mnemonic);
            }

            return new ParsedELFBinary(code, data, instructions, entry_point);
        }
    }
}
