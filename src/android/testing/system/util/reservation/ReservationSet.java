package android.testing.system.util.reservation;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class ReservationSet {

	private List<Reservation> rSet;
	
	public ReservationSet() {
		rSet = new LinkedList<Reservation>();
	}
	
	public boolean addReservation(Reservation r) {
//		System.out.println("[beforeAdd] r's size is " + rSet.size());
		print("beforeAdd");
		int i = legal(r);
		if(i != -1) {
//			if(i == 0) {
//				join(r, rSet.get(i));
//			} else if(i == rSet.size() - 1) {
//				join(rSet.get(i), r);
//			} else {
//				join(join(r, rSet.get(i)), rSet.get(i + 1));
//			}
			rSet.add(i, r);
			
//			int index = clear();
//			if(index > 0) {
//				for(int j = 0; j < index; j++) {
//					rSet.remove(j);
//				}
//			}
			clear();
//			System.out.println("[afterAdd] r's size is " + rSet.size());
			print("afterAdd");
			return true;
		} else {
//			System.out.println("[afterAdd] r's size is " + rSet.size());
			clear();
			print("afterAdd");
			return false;
		}
	}
	
	private int legal(Reservation r) {
		if(rSet.size() == 0) return 0;
		for(int i = 0; i < rSet.size(); i++) {
			Reservation r1 = rSet.get(i);
			if(r1.getStartTime() >= r.getEndTime()) return i;
			if(r.getStartTime() >= r1.getStartTime() && r.getStartTime() <= r1.getEndTime()
					|| r.getEndTime() >= r1.getStartTime() && r.getEndTime() <= r1.getEndTime()
					|| r.getStartTime() <= r1.getStartTime() && r.getEndTime() >= r1.getEndTime())
				return -1;
		}
		return rSet.size() - 1;
	}
	
	private void clear() {
		int index = cut();
		if(index > 0) {
			for(int j = 0; j < index; j++) {
				rSet.remove(j);
			}
		}
	}
	
	private int cut() {
		java.text.SimpleDateFormat f = new java.text.SimpleDateFormat("yyyyMMddHHmm");
		long now = Long.parseLong(f.format(new Date()));
		int i;
		for(i = 0; i < rSet.size(); i++) {
			Reservation r = rSet.get(i);
			if(r.getStartTime() >= now) break;
			if(r.getEndTime() <= now) ;
			else {
				r.setStartTime(now);
				break;
			}
		}
		return i;
	}
	
	private void print(String info) {
		for(Reservation r : rSet) {
			System.out.println("[" + info + "]:" + r.getUsername() + "-" + r.getStartTime() + "-" + r.getEndTime());
		}
	}
}
