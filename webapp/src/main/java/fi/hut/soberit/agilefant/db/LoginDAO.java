package fi.hut.soberit.agilefant.db;

import java.util.List;

import fi.hut.soberit.agilefant.model.Login;
import fi.hut.soberit.agilefant.model.User;

public interface LoginDAO extends GenericDAO<Login> {

    public List<Login> retrieveLoginsByUser(User user);
}

