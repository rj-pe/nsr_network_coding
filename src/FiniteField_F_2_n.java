import java.util.logging.Level;
import java.util.logging.Logger;
import cc.redberry.rings.*;
import cc.redberry.rings.poly.*;
import cc.redberry.rings.poly.univar.*;
import cc.redberry.rings.poly.multivar.*;
import cc.redberry.rings.bigint.BigInteger;

import static cc.redberry.rings.poly.PolynomialMethods.*;
import static cc.redberry.rings.Rings.*;




public class FiniteField_F_2_n {

    private FiniteField<UnivariatePolynomialZp64> gf2_q;
    private int power = 4;
    private int elementsCount = 16;
    private int[] degreesAsElements = null;
    private int[] elemsAsPEDegrees = null;
    private Logger logger;

    private static int[][] elementsAsPrimeElementDegrees = new int[][] {
            { 0 }, { 0 }, { 0 }, 										// not implemented
            { -1, 0, 1, 3, 2, 6, 4, 5 },									// for F 2^3
            { -1, 0, 1, 4, 2, 8, 5, 10, 3, 14, 9, 7, 6, 13, 11, 12 },		// for F 2^4
    };

    private static int[][] primeElementDegreesAsElements = new int[][] {
            { 0 }, { 0 }, { 0 }, // not implemented
            { 1, 2, 4, 3, 6, 7, 5 },	// for F 2^3
            { 1, 2, 4, 8, 3, 6, 12, 11, 5, 10, 7, 14, 15, 13, 9, 1 },	// for F 2^4
    };


    public static FiniteField_F_2_n getInstance() {
        return getInstance(4);
    }

    public static FiniteField_F_2_n getInstance(int n) {
        return new FiniteField_F_2_n(n);
    }

    private FiniteField_F_2_n(int n) {
        this.power = n;
        this.elementsCount = (int) Math.pow(2, n);
        // TODO replace with call to GF() constructor
        //this.gf2_q = Rings.GF(2, n);
        this.elemsAsPEDegrees = elementsAsPrimeElementDegrees[n];
        this.degreesAsElements = primeElementDegreesAsElements[n];
        this.logger =  Logger.getLogger("log");
    }

    public int getPower() {
        return power;
    }

    public int getElementsCount() {
        return elementsCount;
    }

    // TODO replace with call to the FiniteField multiply() method
    public int multiply(int a, int b) {
        if (a % elementsCount == 0 || b % elementsCount == 0) {
            return 0;
        }
        try{
            return degreesAsElements[(elemsAsPEDegrees[a % elementsCount] + elemsAsPEDegrees[b % elementsCount]) % (elementsCount - 1)];
        } catch (Exception ex){
            logger.log(Level.INFO, ex.toString(), ex);
            return -1;
        }
    }

    // TODO replace with call to the FiniteField add() method
    public int add(int a, int b) {
        return (a % elementsCount) ^ (b % elementsCount);
    }

    public Packet multiplyPacketBy(Packet packet, int by) {
        for (int i = 0; i < packet.header.length; i++) {
            packet.header[i] = multiply(packet.header[i], by);
        }
        for (int i = 0; i < packet.body.length; i++) {
            packet.body[i] = multiply(packet.body[i], by);
        }
        return packet;
    }

    public Packet addPackets(Packet one, Packet two) {
        for (int i = 0; i < one.header.length; i++) {
            one.header[i] = add(one.header[i], two.header[i]);
        }
        for (int i = 0; i < one.body.length; i++) {
            one.body[i] = add(one.body[i], two.body[i]);
        }
        return one;
    }

    // TODO this method will be replaced by a call to the FiniteField reciprocal() method
    public int complementByOne(int value) {
        int result = -1;
        if (value != 0) {
            result = degreesAsElements[elementsCount - 1 - elemsAsPEDegrees[value % elementsCount]];
        }
        return result;
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
