package de.qaware.mercury.mercury.business.reservation.impl;

import de.qaware.mercury.mercury.business.reservation.Interval;
import de.qaware.mercury.mercury.business.reservation.Reservation;
import de.qaware.mercury.mercury.business.reservation.ReservationService;
import de.qaware.mercury.mercury.business.reservation.Slot;
import de.qaware.mercury.mercury.business.reservation.SlotService;
import de.qaware.mercury.mercury.business.shop.Shop;
import de.qaware.mercury.mercury.business.time.Clock;
import de.qaware.mercury.mercury.storage.reservation.ReservationRepository;
import de.qaware.mercury.mercury.util.Lists;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class ReservationServiceImpl implements ReservationService {
    private final SlotService slotService;
    private final ReservationRepository reservationRepository;
    private final Clock clock;

    @Override
    public List<Slot> listSlots(Shop shop) {
        LocalDate today = clock.today();
        // Find all reservations in the time range
        List<Reservation> reservations = reservationRepository.findReservationsForShop(shop.getId(), today.atTime(0, 0), today.atTime(23, 59));
        // Now generate slots. The time ranges which also have reservations are marked as unavailable
        return slotService.generateSlots(today, today, shop.getSlotConfig(), mapReservations(reservations));
    }

    private List<Interval> mapReservations(List<Reservation> reservations) {
        return Lists.map(reservations, r -> Interval.of(r.getStartTime().toLocalDateTime(), r.getEndTime().toLocalDateTime()));
    }
}
