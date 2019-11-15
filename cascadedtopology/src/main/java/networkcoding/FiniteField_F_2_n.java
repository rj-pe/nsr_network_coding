package networkcoding;

import muni.fi.gf2n.classes.GF2N;

import java.io.Serializable;


public class FiniteField_F_2_n implements Serializable {

    private GF2N gf;

    private int power = 4;
    private int elementsCount;


    public FiniteField_F_2_n(int n) {
        /*
         *   A list of irreducible polynomials for construction of GF2N.
         *   2^1,   2^2,   2^3,   2^4,   2^5,   2^6,   2^7,   2^8,   2^9,   2^a,   2^b
         *   2  ,   7  ,   11 ,   19 ,   37 ,   67 ,   131,   283,   515,  1033,  2053
         *   See page 378 in Lidl & Niederreiter.
         */
        long[] irreducible = {-1, 2, 7, 11, 19, 37, 67, 131, 283, 515, 1033, 2053};
        this.power = n;
        this.elementsCount = (int) Math.pow(2, n);
        this.gf = new GF2N(irreducible[n]);
    }

    public int getPower() {
        return power;
    }

    int getElementsCount() {
        return elementsCount;
    }

    private int multiply(int a, int b) {
        return (int) gf.multiply((long) a, (long) b);
    }

    private int add(int a, int b) {
        return (int) gf.add((long) a, (long) b);
    }

    Packet multiplyPacketBy(Packet packet, int by) {
        for (int i = 0; i < packet.header.length; i++) {
            packet.header[i] = multiply(packet.header[i], by);
        }
        for (int i = 0; i < packet.body.length; i++) {
            packet.body[i] = multiply(packet.body[i], by);
        }
        return packet;
    }

    void addPackets(Packet one, Packet two) {
        for (int i = 0; i < one.header.length; i++) {
            one.header[i] = add(one.header[i], two.header[i]);
        }
        for (int i = 0; i < one.body.length; i++) {
            one.body[i] = add(one.body[i], two.body[i]);
        }
    }

    int reciprocal(int value) {
        int result = 0;
        if (value != 0) {
            result = (int) gf.invert((long) value);
        }
        return result;
    }

    GF2N getGF() {
        return gf;
    }

    public void printSummationTable() {
        int n = elementsCount;
        System.out.print(" + |");
        for (int i = 1; i < n; i++) System.out.print(String.format("%3d", i));
        System.out.print("\n---|");
        for (int i = 1; i < n; i++) System.out.print("---");
        System.out.println();
        for (int i = 1; i < n; i++) {
            System.out.print(String.format("%3d|", i));
            for (int j = 1; j < n; j++) {
                System.out.print(String.format("%3d", add(i, j)));
            }
            System.out.println();
        }
    }

    public void printMultiplicationTable() {
        int n = elementsCount;
        System.out.print(" * |");
        for (int i = 1; i < n; i++) System.out.print(String.format("%3d", i));
        System.out.print("\n---|");
        for (int i = 1; i < n; i++) System.out.print("---");
        System.out.println();
        for (int i = 1; i < n; i++) {
            System.out.print(String.format("%3d|", i));
            for (int j = 1; j < n; j++) {
                System.out.print(String.format("%3d", multiply(i, j)));
            }
            System.out.println();
        }
    }
}
