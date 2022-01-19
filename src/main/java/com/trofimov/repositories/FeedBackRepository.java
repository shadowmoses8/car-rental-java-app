package com.trofimov.repositories;

import java.util.List;

import javax.persistence.TypedQuery;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.trofimov.entities.Feedback;

@Repository
@Transactional
public class FeedBackRepository {
	@Autowired
	SessionFactory sessionFactory;


	public void addFeedback(Feedback feedback) {
		Session session = this.sessionFactory.getCurrentSession();
		if (feedback != null) {
			session.persist(feedback);
		}
	}


	public Feedback getFeedback(String userName) {

		String sql = "From " + Feedback.class.getName() + " WHERE userName=:userName";
		Session session = this.sessionFactory.getCurrentSession();
		TypedQuery<Feedback> query = session.createQuery(sql, Feedback.class);
		query.setParameter("userName", userName);
		return query.getSingleResult();
	}


	public List<Feedback> getFeedbacks() {
		List<Feedback> list = null;

		String sql = "FROM " + Feedback.class.getName();
		Session session = this.sessionFactory.getCurrentSession();
		list = session.createQuery(sql, Feedback.class).getResultList();
		return list;
	}
}
