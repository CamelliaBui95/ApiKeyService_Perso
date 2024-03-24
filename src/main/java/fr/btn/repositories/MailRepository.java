package fr.btn.repositories;

import fr.btn.entities.MailEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.RequestScoped;

@RequestScoped
public class MailRepository implements PanacheRepositoryBase<MailEntity, Integer> {
    public int getMailCountByMonth(String apiKey, int month) {
        return (int) count("sender.apiKey=?1 and MONTH(date)=?2", apiKey, month);
    }
}
