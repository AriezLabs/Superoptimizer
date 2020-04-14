import sys

if len(sys.argv) != 2:
    print(f"usage: python {sys.argv[0]} inputfile")
    exit()

with open(sys.argv[1]) as f:
    while f.readline().strip() == '&':
        c1 = f.readline()

        f3 = "null"
        f7 = "null"

        if 'imm[31' in c1:
            t = 'u'
            f.readline()

        elif 'imm[20' in c1:
            t = 'j'
            f.readline()

        elif 'imm[11:0' in c1:
            t = 'i'
            f.readline()
            f3 = "0b" + f.readline().split("{")[-1].split("}")[0]
            f.readline()

        elif 'imm[12' in c1:
            t = 'b'
            f.readline()
            f.readline()
            f3 = "0b" + f.readline().split("{")[-1].split("}")[0]
            f.readline()

        elif 'imm[11:5' in c1:
            t = 's'
            f.readline()
            f.readline()
            f3 = "0b" + f.readline().split("{")[-1].split("}")[0]
            f.readline()

        else:
            t = 'r'
            f7 = "0b" + c1.split("{")[-1].split("}")[0]
            f.readline()
            f.readline()
            f3 = "0b" + f.readline().split("{")[-1].split("}")[0]
            f.readline()

        l = f.readline()
        opcode = l.split("&")[0].split("{")[-1][:7]
        mnemonic = l.split("&")[1].split("\\")[0].strip().lower()
        f.readline()
        f.readline()
        f.readline()    
    
        print(f'new InstructionType("{mnemonic}", "{t}", 0b{opcode}, {f3}, {f7}),')
