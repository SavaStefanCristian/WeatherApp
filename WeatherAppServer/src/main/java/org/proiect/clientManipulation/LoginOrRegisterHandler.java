package org.proiect.clientManipulation;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import org.proiect.persistence.connection.ParameterPair;
import org.proiect.persistence.dao.EntityDao;
import org.proiect.persistence.dao.RoleDao;
import org.proiect.persistence.model.UserEntity;
import org.proiect.persistence.model.UserRole;
import org.proiect.persistence.model.UserRoleId;
import org.proiect.persistence.model.enums.Role;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Optional;

public class LoginOrRegisterHandler {
    EntityDao<UserEntity> userDao;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    public Optional<Long> start(EntityDao<UserEntity> userDao, ObjectInputStream in, ObjectOutputStream out) {
            this.userDao = userDao;
            this.in = in;
            this.out = out;

            Optional<Long> userId = Optional.empty();
        SerializableMessage loginOrRegisterMessage = new SerializableMessage("\n Hello!");
            Boolean isNotConnected = true;
            while (isNotConnected) {
                try {
                    loginOrRegisterMessage.message +="\n  1. Login \n  2. Register \n  0. Exit\n";
                    this.out.writeObject(loginOrRegisterMessage);
                    this.out.writeObject(new SerializableMessage("OK"));
                    loginOrRegisterMessage = new SerializableMessage("");
                    SerializableMessage loginOrRegisterResponse = (SerializableMessage) this.in.readObject();
                    if (loginOrRegisterResponse.equals("0")) {
                        return userId;
                    }
                    if (loginOrRegisterResponse.equals("1")) {
                        userId = login();
                        if(userId.isPresent()) isNotConnected=false;
                        continue;
                    }
                    if(loginOrRegisterResponse.equals("2")){
                        userId = register();
                        if(userId.isPresent()) {
                            userId = login();
                            if(userId.isPresent()) isNotConnected=false;
                        }
                        continue;
                    }

                } catch (IOException | ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
            return userId;
    }

    private Optional<Long> login() {
        Optional<Long> usrId = Optional.empty();
        Argon2 argon2 = Argon2Factory.create();
        SerializableMessage startRegisterMessage = new SerializableMessage("");
        while (true) {
            try {
                startRegisterMessage.message += "\n Login:\n  0. Return\nUsername : ";
                this.out.writeObject(startRegisterMessage);
                this.out.writeObject(new SerializableMessage("OK"));
                startRegisterMessage = new SerializableMessage("");
                SerializableMessage username;
                username = (SerializableMessage) this.in.readObject();
                if (username.equals("0")) {
                    return usrId;
                }

                SerializableMessage passwordMessage = new SerializableMessage("Password : ");
                this.out.writeObject(passwordMessage);
                this.out.writeObject(new SerializableMessage("OK"));
                SerializableMessage password;
                password = (SerializableMessage) this.in.readObject();
                if (password.equals("0")) {
                    return usrId;
                }

                UserEntity thisUser = userDao.findFirstByParams(UserEntity.class, new ParameterPair("username", username.message));

                if (thisUser == null) {
                    startRegisterMessage.message += "\nLogin failed! Try again!";
                    continue;
                }
                if(argon2.verify(thisUser.getPassHash(),password.message)) {
                    usrId = Optional.of(thisUser.getId());
                    return usrId;
                }

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
    private Optional<Long> register() {
        Optional<Long> usrId = Optional.empty();
        Argon2 argon2 = Argon2Factory.create();
        SerializableMessage startRegisterMessage = new SerializableMessage("");
        SerializableMessage username;
        SerializableMessage password;
        while (true) {
            try {
                startRegisterMessage.message += "\nRegister:\n  0. Return\nUsername : ";
                this.out.writeObject(startRegisterMessage);
                this.out.writeObject(new SerializableMessage("OK"));
                startRegisterMessage = new SerializableMessage("");
                username = (SerializableMessage) this.in.readObject();
                if (username.equals("0")) {
                    return usrId;
                }
                if(username.message.length() < 8) {
                    startRegisterMessage.message += "\nUsername too short! Try again!\n";
                    continue;
                }

                UserEntity thisUser = userDao.findFirstByParams(UserEntity.class, new ParameterPair("username", username.message));

                if (thisUser != null) {
                    startRegisterMessage.message += "\nUser already exists!";
                    continue;
                }
                else {
                    break;
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        startRegisterMessage = new SerializableMessage("");
        while (true) {
            try {
                startRegisterMessage.message += "Password : ";
                this.out.writeObject(startRegisterMessage);
                this.out.writeObject(new SerializableMessage("OK"));
                startRegisterMessage = new SerializableMessage("");
                password = (SerializableMessage) this.in.readObject();
                if (password.equals("0")) {
                    return usrId;
                }
                if(password.message.length() < 8) {
                    startRegisterMessage.message += "\nPassword too short! Try again!\n";
                    continue;
                }

                UserEntity newUser = new UserEntity();
                newUser.setUsername(username.message);
                newUser.setPassHash(argon2.hash(3,1024,1,password.message));
                userDao.save(newUser);
                UserEntity createdUser = userDao.findFirstByParams(UserEntity.class, new ParameterPair("username", username.message));
                if(createdUser == null) {
                    System.err.println("Could not create new user!");
                    return usrId;
                }

                RoleDao roleDao = new RoleDao(userDao.getConnection());
                EntityDao<UserRole> userRoleDao = new EntityDao<UserRole>(userDao.getConnection());
                org.proiect.persistence.model.RoleEntity newRole = roleDao.findRoleByCode(Role.USER.getCode());
                if(newRole == null) {
                    System.err.println("Could not create new user! Role 'USER' not found!'");
                    return usrId;
                }
                UserRole newUserRole = new UserRole();
                UserRoleId newUserRoleId = new UserRoleId();
                newUserRoleId.setRoleId(newRole.getId());
                newUserRoleId.setUserId(createdUser.getId());
                newUserRole.setId(newUserRoleId);
                newUserRole.setUser(createdUser);
                newUserRole.setRole(newRole);
                userRoleDao.save(newUserRole);
                usrId = Optional.of(newUser.getId());
                return usrId;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }


}
