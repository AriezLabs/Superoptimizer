package parser;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.util.Arrays;

public class ELFParser {
    private static final int SIZEOFUINT64STAR = 8;
    // page-aligned ELF header for storing file header (64 bytes),
    // program header (56 bytes), and code length (8 bytes)
    private static final int ELF_HEADER_LEN = 4096;

    private static final InstructionType[] decodeTable = {
            // f3/f7 are null iff this type of instruction does not have a f3/f7 code
            new InstructionType("ecall", "i", 0b1110011, null, null),
            new InstructionType("ld", "i", 0b0000011, 0b011, null),
            new InstructionType("sd", "s", 0b0100011, 0b011, null),
            new InstructionType("mul", "r", 0b0110011, 0b000, 0b0000001),
            new InstructionType("divu", "r", 0b0110011, 0b101, 0b0000001),
            new InstructionType("remu", "r", 0b0110011, 0b111, 0b0000001),
            new InstructionType("sltu", "r", 0b0110011, 0b011, 0b0000000),
            new InstructionType("jalr", "i", 0b1100111, 0b000, null),
            new InstructionType("lui", "u", 0b0110111, null, null),
            new InstructionType("addi", "i", 0b0010011, 0b000, null),
            new InstructionType("sub", "r", 0b0110011, 0b000, 0b0100000),
            new InstructionType("add", "r", 0b0110011, 0b000, 0b0000000),
            new InstructionType("beq", "b", 0b1100011, 0b000, null),
            new InstructionType("jal", "j", 0b1101111, null, null),
    };

    private static Instruction decode(int binary) {
        for (InstructionType type : decodeTable)
            if (type.matches(binary))
                return new Instruction(type, binary);

        throw new RuntimeException(String.format("unknown instruction 0x%08X", binary));
    }

    private static int byteArrayToLeInt(byte[] encodedValue) {
        int value = (encodedValue[3] << (Byte.SIZE * 3));
        value |= (encodedValue[2] & 0xFF) << (Byte.SIZE * 2);
        value |= (encodedValue[1] & 0xFF) << (Byte.SIZE);
        value |= (encodedValue[0] & 0xFF);
        return value;
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
            long entry_point = buffer.getLong(10 * SIZEOFUINT64STAR);
            long binary_length = buffer.getLong(12 * SIZEOFUINT64STAR);
            long code_length = buffer.getLong(15 * SIZEOFUINT64STAR);

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
                System.out.println("Decoding instruction at " + String.format("0x%x", i) + "...");
                byte[] insBytes = Arrays.copyOfRange(code, i, i + 4);
                int insInt = byteArrayToLeInt(insBytes);
                instructions[i / 4] = decode(insInt);
                System.out.println("Got " + instructions[i / 4].mnemonic);
            }

            return new ParsedELFBinary(code, data, instructions, entry_point);
        }
    }
}
