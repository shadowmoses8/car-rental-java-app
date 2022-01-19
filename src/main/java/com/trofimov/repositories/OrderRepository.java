package com.trofimov.repositories;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.trofimov.entities.Order;
import com.trofimov.entities.OrderItem;
import com.trofimov.entities.User;
import com.trofimov.models.OrderDaily;
import com.trofimov.models.OrderInfo;
import com.trofimov.models.OrderStatistics;
import com.trofimov.models.OrderUsersDetail;

@Repository
@Transactional
public class OrderRepository {

	@Autowired
	SessionFactory sessionFactory;


	public void addOrder(Order order, List<Long> carsId) {

		Session session = this.sessionFactory.getCurrentSession();
		Long orderId = (Long) session.save(order);

		for (Long carId : carsId) {
			OrderItem item = new OrderItem(orderId, carId);
			session.save(item);
		}
	}


	@Transactional(propagation = Propagation.MANDATORY, rollbackFor = Exception.class)
	public void updateOrderStatus(Long id, String status) {
		Session session = this.sessionFactory.getCurrentSession();
		Order order = session.get(Order.class, id);
		order.setStatus(status);
	}


	public Order getOrder(Long id) {
		Session session = this.sessionFactory.getCurrentSession();
		return session.get(Order.class, id);

	}


	public List<Order> getOrders() {
		List<Order> orderList = null;

		Session session = this.sessionFactory.getCurrentSession();
		String sql = "from " + Order.class.getName();
		Query<Order> query = session.createQuery(sql, Order.class);
		orderList = query.getResultList();

		return orderList;
	}


	public List<Order> getOrdersByUser(Long userId) {
		List<Order> orderList = null;

		String sql = "SELECT e FROM " + Order.class.getName() + " e WHERE e.userId =:" + Order.ATTRIBUTE_USER_ID;
		Session session = this.sessionFactory.getCurrentSession();
		Query<Order> query = session.createQuery(sql, Order.class);
		query.setParameter(Order.ATTRIBUTE_USER_ID, userId);
		orderList = query.getResultList();

		return orderList;
	}

	public List<OrderUsersDetail> getOrdersDetails() {
		List<OrderUsersDetail> ordDetList = null;

		String sql = "Select new " + OrderUsersDetail.class.getName() + "(a.id, b.name, a.bill)" + " from "
				+ Order.class.getName() + " a inner join " + User.class.getName() + " b ON a.userId=b.id ";
		Session session = this.sessionFactory.getCurrentSession();
		Query<OrderUsersDetail> query = session.createQuery(sql, OrderUsersDetail.class);
		ordDetList = query.getResultList();

		return ordDetList;
	}


	public List<OrderStatistics> getOrdersStatisticByUsers() {
		List<OrderStatistics> ordStat = null;

		String sql = "SELECT NEW " + OrderStatistics.class.getName() + " (b.id, b.name, SUM(a.bill)) " + " FROM "
				+ Order.class.getName() + " a INNER JOIN " + User.class.getName() + " b ON a.userId=b.id "
				+ "GROUP BY b.id";
		Session session = this.sessionFactory.getCurrentSession();
		Query<OrderStatistics> query = session.createQuery(sql, OrderStatistics.class);
		ordStat = query.getResultList();

		return ordStat;
	}


	public void getCompToAvg() {

		String sql = "SELECT id, userId, bill, AVG(bill) " + " OVER (PARTITION BY userId) FROM car_rental.orders";
		Session session = this.sessionFactory.getCurrentSession();

		@SuppressWarnings("unchecked")

		List<Object[]> oList = session.createNativeQuery(sql).getResultList();
		for (Object[] objects : oList) {
			for (int i = 0; i < objects.length; i++) {
				System.out.print(objects[i].toString() + " | ");
			}
			System.out.println("");
		}
	}


	public List<OrderDaily> getOrderReports() {

		String sql = "SELECT new " + OrderDaily.class.getName() + " (SUM(a.bill), COUNT(a.id),a.start_date) " + "FROM "
				+ Order.class.getName() + " a GROUP BY a.start_date ORDER BY a.start_date DESC";
		Session session = this.sessionFactory.getCurrentSession();
		Query<OrderDaily> query = session.createQuery(sql, OrderDaily.class);

		return query.getResultList();
	}


	public List<OrderInfo> getOrdersInfo() {
		// Mapping native query result to POJO model class
		String sql = "Select id, userId, bill FROM car_rental.orders ";
		Session session = this.sessionFactory.getCurrentSession();

		@SuppressWarnings("unchecked")
		Query<OrderInfo> query = session.createNativeQuery(sql, "OrderInfoMapping");
		List<OrderInfo> stats = query.getResultList();

		return stats;
	}
}
