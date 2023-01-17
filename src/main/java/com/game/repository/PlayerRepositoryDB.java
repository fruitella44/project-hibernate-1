package com.game.repository;

import com.game.entity.Player;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import javax.annotation.PreDestroy;
import java.util.List;
import java.util.Optional;
@Repository(value = "db")
public class PlayerRepositoryDB implements IPlayerRepository {
    private final SessionFactory sessionFactory;

    public PlayerRepositoryDB() {
        sessionFactory = new Configuration()
                .addAnnotatedClass(Player.class)
                .buildSessionFactory();

        //Using hibernate.properties file to access to Database
        //Hide in resources to secure a password from Database
    }

    @Override
    public List<Player> getAll(int pageNumber, int pageSize) {
        try (Session session = sessionFactory.openSession()) {
            NativeQuery<Player> getAllPlayers = session.createNativeQuery("SELECT * FROM rpg.player", Player.class);
            getAllPlayers.setFirstResult(pageNumber * pageSize);
            getAllPlayers.setMaxResults(pageSize);

            return getAllPlayers.list();
        }
    }

    @Override
    public int getAllCount() {
        try (Session session = sessionFactory.openSession()) {
            Query<Long> getCount = session.createNamedQuery("player_getAllCount", Long.class);
            return Math.toIntExact(getCount.uniqueResult());
        }
    }

    @Override
    public Player save(Player player) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            session.save(player);
            transaction.commit();

            return player;
        }
    }

    @Override
    public Player update(Player player) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            session.update(player);
            transaction.commit();

            return player;
        }
    }

    @Override
    public Optional<Player> findById(long id) {
        try (Session session = sessionFactory.openSession()) {
            Player player = session.find(Player.class, id);
            return Optional.of(player);
        }
    }

    @Override
    public void delete(Player player) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            session.delete(player);
            transaction.commit();
        }

    }

    @PreDestroy
    public void beforeStop() {
        sessionFactory.close();
    }
}