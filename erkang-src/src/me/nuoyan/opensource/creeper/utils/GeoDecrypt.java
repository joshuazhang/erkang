package me.nuoyan.opensource.creeper.utils;

public class GeoDecrypt {

	public static double[] getLonLat(String C) {

		int digi = 16;
		int add = 10;
		int plus = 7;
		int cha = 36;
		int I = -1;
		int H = 0;
		String B = "";
		int J = C.length();
		char G = C.charAt(J - 1);
		C = C.substring(0, J - 1);
		J--;
		for (int E = 0; E < J; E++) {
			int D = Integer.parseInt(C.substring(E, E+1), cha) - add;
			if (D >= add) {
				D = D - plus;
			}
			B += Integer.parseInt(D + "", cha) + "";
			// B += (D).toString(cha);
			if (D > H) {
				I = E;
				H = D;
			}
		}
		long A = Long.parseLong(B.substring(0, I), digi);
		long F = Long.parseLong(B.substring(I + 1), digi);
		double L = (A + F - G) / 2d;
		double K = (F - L) / 100000d;
		L /= 100000;
		double[] res = new double[2];
		res[0] = L;
		res[1] = K;
		return res;
	}

	public static void main(String[] args) {
		double[] res = getLonLat("HEUTSVZVVSCWUK");
		System.out.println(res[0] + " - " + res[1]);
	}

}
