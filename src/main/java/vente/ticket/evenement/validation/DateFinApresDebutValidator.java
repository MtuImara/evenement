package vente.ticket.evenement.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import vente.ticket.evenement.dto.evenement.EvenementRequest;

public class DateFinApresDebutValidator implements ConstraintValidator<DateFinApresDebut, EvenementRequest> {

    @Override
    public boolean isValid(EvenementRequest value, ConstraintValidatorContext context) {
        if (value == null || value.dateDebut() == null || value.dateFin() == null) {
            return true;
        }
        return value.dateFin().isAfter(value.dateDebut());
    }
}
