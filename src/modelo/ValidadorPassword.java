package modelo;

public class ValidadorPassword {
    
    public static String evaluar(String password) {
        if (password == null || password.isEmpty()) return "Vacia";
        
        boolean tieneLargo = password.length() >= 8;
        boolean tieneMayuscula = password.matches(".*[A-Z].*");
        boolean tieneNumero = password.matches(".*[0-9].*");
        boolean tieneEspecial = password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*");

        int puntos = 0;
        if (tieneLargo) puntos++;
        if (tieneMayuscula) puntos++;
        if (tieneNumero) puntos++;
        if (tieneEspecial) puntos++;

        if (puntos <= 2) return "Debil";
        if (puntos == 3) return "Media";
        return "Fuerte"; // Cumple los 4
    }
}