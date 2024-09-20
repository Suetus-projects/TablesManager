/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Functional;

/**
 *
 * @author dan_deb
 */
public class Fractions {
    
    public static String neededToReachOne(String[] fraction) throws Exception {
        var suma = sumFractions(fraction).split("/");
        long num = Long.parseLong(suma[0]);
        long denom = Long.parseLong(suma[1]);
        if (num > denom)
            throw new RuntimeException("Fraction bigger than 1");
        num = denom - num;
        return "" + num + "/" + denom;
    }
    
    public static String sumFractions(String[] fractions) throws Exception {
        long t_num = 0;
        long t_denom = 1;
        for (String frac : fractions) {
            var num_denom = frac.split("/");
            long next_num = Long.parseLong(num_denom[0]);
            long next_denom = Long.parseLong(num_denom[1]);
            
            if (t_denom == next_denom) {
                t_num += next_num;
                continue;
            }
            
            long new_denom = t_denom * next_denom;
            t_num = (t_denom * next_num) + (next_denom * t_num);
            t_denom = new_denom;
        }
        
        var reduced = reduceFraction(t_num, t_denom);
        t_num = reduced[0];
        t_denom = reduced[1];
        
        return "" + t_num + "/" + t_denom;
    }
    
    
    
    public static long[] reduceFraction(long num, long denom) {
        long div = gcd(num, denom);
        num = num / div;
        denom = denom / div;
        return new long[]{num, denom};
    }
    
    public static long gcd(long a, long b) {
        if (b == 0)
            return a;
        return gcd(b, a % b);
    }
}
