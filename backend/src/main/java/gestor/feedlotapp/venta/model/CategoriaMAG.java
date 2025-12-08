package gestor.feedlotapp.venta.model;


import gestor.feedlotapp.entities.Animal;

import java.math.BigDecimal;

public enum CategoriaMAG {


    NOVILLITO_M_300_390("NOVILLITOS EyB M. 300/390", true, 300, 390),
    NOVILLITO_P_391_430("NOVILLITOS EyB P. 391/430", true, 391, 430),
    NOVILLO_431_460("NOVILLOS Mest.EyB 431/460", true, 431, 460),
    NOVILLO_461_490("NOVILLOS Mest.EyB 461/490", true, 461, 490),
    NOVILLO_491_520("NOVILLOS Mest.EyB 491/520", true, 491, 520),
    NOVILLO_MAS_520("NOVILLOS Mest.EyB + 520", true, 521, 100000),


    VAQUILLONA_270_390("VAQUILLONAS EyB M.270/390", false, 270, 390),
    VAQUILLONA_391_430("VAQUILLONAS EyB P.391/430", false, 391, 430),
    VACA_431_MAS("VACAS (≥431kg) Buenas/Regulares", false, 431, 100000);

    private final String etiquetaMAG;
    private final boolean macho;
    private final double kgMin;
    private final double kgMax;

    CategoriaMAG(String etiquetaMAG, boolean macho, double kgMin, double kgMax) {
        this.etiquetaMAG = etiquetaMAG;
        this.macho = macho;
        this.kgMin = kgMin;
        this.kgMax = kgMax;
    }

    public String etiquetaMAG() { return etiquetaMAG; }

    public static String clasificar(Animal a) {
        boolean esMacho = a.getSexo();
        BigDecimal kg = a.getPesoActual();
        if (kg == null) kg = BigDecimal.ZERO;


        final BigDecimal KG_270 = new BigDecimal("270");
        final BigDecimal KG_300 = new BigDecimal("300");
        final BigDecimal KG_390 = new BigDecimal("390");
        final BigDecimal KG_430 = new BigDecimal("430");
        final BigDecimal KG_460 = new BigDecimal("460");
        final BigDecimal KG_490 = new BigDecimal("490");
        final BigDecimal KG_520 = new BigDecimal("520");

        if (esMacho) {
            if (kg.compareTo(KG_300) < 0) return "NOVILLITOS livianos (<300)";
            if (kg.compareTo(KG_390) < 0) return CategoriaMAG.NOVILLITO_M_300_390.etiquetaMAG;
            if (kg.compareTo(KG_430) < 0) return CategoriaMAG.NOVILLITO_P_391_430.etiquetaMAG;
            if (kg.compareTo(KG_460) < 0) return CategoriaMAG.NOVILLO_431_460.etiquetaMAG;
            if (kg.compareTo(KG_490) < 0) return CategoriaMAG.NOVILLO_461_490.etiquetaMAG;
            if (kg.compareTo(KG_520) < 0) return CategoriaMAG.NOVILLO_491_520.etiquetaMAG;
            return CategoriaMAG.NOVILLO_MAS_520.etiquetaMAG;

        } else {
            if (kg.compareTo(KG_270) < 0) return "VAQUILLONAS livianas (<270)";
            if (kg.compareTo(KG_390) < 0) return CategoriaMAG.VAQUILLONA_270_390.etiquetaMAG;
            if (kg.compareTo(KG_430) < 0) return CategoriaMAG.VAQUILLONA_391_430.etiquetaMAG;
            return CategoriaMAG.VACA_431_MAS.etiquetaMAG;
        }
    }

}

