package pe.edu.upc.demosm2.dtos;

public class JwtRequestDTO {
    private String emailPersona;
    private String passwordPersona;

    public JwtRequestDTO() {
    }

    public String getEmailPersona() {
        return this.emailPersona;
    }

    public void setEmailPersona(String emailPersona) {
        this.emailPersona = emailPersona;
    }

    public String getPasswordPersona() {
        return this.passwordPersona;
    }

    public void setPasswordPersona(String passwordPersona) {
        this.passwordPersona = passwordPersona;
    }
}
