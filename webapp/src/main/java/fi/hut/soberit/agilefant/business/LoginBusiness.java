package fi.hut.soberit.agilefant.business;

import java.util.List;

import fi.hut.soberit.agilefant.model.Login;
import fi.hut.soberit.agilefant.model.User;

public interface LoginBusiness extends GenericBusiness<Login> {
    public List<Login> retrieveLoginsByUser(User user);
}
