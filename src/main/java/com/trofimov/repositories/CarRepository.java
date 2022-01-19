package com.trofimov.repositories;

import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Root;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.trofimov.entities.Car;
import com.trofimov.entities.OrderItem;
import com.trofimov.models.CarStatistic;

@Repository
@Transactional
public class CarRepository {

	@Autowired
	SessionFactory sessionFactory;


	public Car getCar(Long id) {

		Session session = this.sessionFactory.getCurrentSession();
		return session.find(Car.class, id);
	}
	

	public List<Car> getCars() {
		List<Car> carList = null;

		Session session = this.sessionFactory.getCurrentSession();
		String sql = "from " + Car.class.getName();
		Query<Car> query = session.createQuery(sql, Car.class);
		carList = query.getResultList();

		return carList;
	}


	public List<CarStatistic> getCarsStatistic() {
		List<CarStatistic> carsStat = null;

		String sql = "Select new " + CarStatistic.class.getName() + " (a.id, a.model, COUNT(b.carId)) FROM "
				+ Car.class.getName() + " a INNER JOIN " + OrderItem.class.getName() + " b ON a.id=b.carId "
				+ " GROUP BY a.id " + " ORDER BY COUNT(b.carId) DESC";
		Session session = this.sessionFactory.getCurrentSession();
		Query<CarStatistic> query = session.createQuery(sql, CarStatistic.class);
		carsStat = query.getResultList();

		return carsStat;

	}


	public List<CarStatistic> getCarUsedN(Long number) {
		List<CarStatistic> carsStatList = null;

		Session session = this.sessionFactory.getCurrentSession();
		String sql = "SELECT new " + CarStatistic.class.getName() + " (a.id, a.model, COUNT(b.carId)) from "
				+ Car.class.getName() + " a INNER JOIN " + OrderItem.class.getName() + " b ON a.id = b.carId "
				+ " GROUP BY a.id " + " HAVING COUNT(b.carId)> :number";
		Query<CarStatistic> query = session.createQuery(sql, CarStatistic.class);
		query.setParameter("number", number);
		carsStatList = query.getResultList();

		return carsStatList;
	}


	public void addCar(Car car) {
		Session session = this.sessionFactory.getCurrentSession();
		session.persist(car);
	}


	@Transactional(propagation = Propagation.MANDATORY)
	public void updateCar(Car car) {
		Session session = this.sessionFactory.getCurrentSession();
		session.update(Car.class.getName(), car);
	}


	@Transactional(propagation = Propagation.MANDATORY)
	public void updatePrices(double coef) {
		Session session = this.sessionFactory.getCurrentSession();

		session.flush();
		String sql = "UPDATE " + Car.class.getName() + " SET cost=cost*" + coef;
		session.createQuery(sql).executeUpdate();

	}


	public void updateCars(Long newAmount) {
		Session session = this.sessionFactory.getCurrentSession();
		CriteriaBuilder cb = session.getCriteriaBuilder();

		// creating update
		CriteriaUpdate<Car> update = cb.createCriteriaUpdate(Car.class);

		// setting root class
		Root<Car> e = update.from(Car.class);

		// setting update and where clause
		update.set(Car.ATTRIBUTE_COST, newAmount);
		update.where(cb.greaterThanOrEqualTo(e.get(Car.ATTRIBUTE_COST), 100L));

		session.createQuery(update).executeUpdate();

	}


	public void deleteCar(Long id) {
		Session session = this.sessionFactory.getCurrentSession();
		Car car = session.get(Car.class, id);
		session.delete(car);
	}
}
