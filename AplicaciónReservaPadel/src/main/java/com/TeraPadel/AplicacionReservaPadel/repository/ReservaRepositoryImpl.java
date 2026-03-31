package com.TeraPadel.AplicacionReservaPadel.repository;

import com.TeraPadel.AplicacionReservaPadel.dto.PeticionBusquedaReserva;
import com.TeraPadel.AplicacionReservaPadel.model.Reserva;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Repository
public class ReservaRepositoryImpl implements ReservaRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Reserva> buscar(final PeticionBusquedaReserva filtro) {
        try {
            final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
            final Long contador = contar(criteriaBuilder, filtro);
            if (contador > 0) {
                return buscar(criteriaBuilder, filtro);
            }
        } catch (Exception ex) {
            log.error("Error to find all: ", ex);
        }
        return Collections.emptyList();
    }

    private Long contar(final CriteriaBuilder criteriaBuilder, final PeticionBusquedaReserva filtro) {
        
        final CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        final Root<Reserva> tablaPrincipal = criteriaQuery.from(Reserva.class);
        criteriaQuery.select(criteriaBuilder.count(tablaPrincipal));
        final List<Predicate> criteriosDeFiltrado = crearElFiltro(criteriaBuilder, tablaPrincipal, filtro);
        final CriteriaQuery<Long> where = criteriaQuery.where(criteriaBuilder.and(
                criteriosDeFiltrado.toArray(new Predicate[criteriosDeFiltrado.size()])));
        final Query consulta = entityManager.createQuery(where);
        final Long contador = (Long) consulta.getSingleResult();
        log.info("Contador {}", contador);
        return contador;
    }

    private static List<Predicate> crearElFiltro(final CriteriaBuilder criteriaBuilder,
            final Root<Reserva> tablaPrincipal,
            final PeticionBusquedaReserva filtro) {
        
        final List<Predicate> condiciones = new ArrayList<>();
        if (filtro.getId_usuario() != null) {
            condiciones.add(criteriaBuilder.equal(tablaPrincipal.get("dni"), filtro.getId_usuario()));
        }
        if (filtro.getFechaCreacionDesde() != null) {
            condiciones.add(
                    criteriaBuilder.greaterThanOrEqualTo(tablaPrincipal.get("fecha"), filtro.getFechaCreacionDesde()));
        }
        if (filtro.getFechaCreacionHasta() != null) {
            condiciones.add(
                    criteriaBuilder.lessThanOrEqualTo(tablaPrincipal.get("fecha"), filtro.getFechaCreacionHasta()));
        }
        return condiciones;
    }

    private List<Reserva> buscar(final CriteriaBuilder criteriaBuilder, final PeticionBusquedaReserva filtro) {
        
        final CriteriaQuery<Reserva> criteriaQuery = criteriaBuilder.createQuery(Reserva.class);
        final Root<Reserva> tablaPrincipal = criteriaQuery.from(Reserva.class);
        criteriaQuery.select(tablaPrincipal);
        final List<Predicate> criteriosDeFiltrado = crearElFiltro(criteriaBuilder, tablaPrincipal, filtro);
        final CriteriaQuery<Reserva> where = criteriaQuery.where(criteriaBuilder.and(
                criteriosDeFiltrado.toArray(new Predicate[criteriosDeFiltrado.size()])));
        where.orderBy(criteriaBuilder.asc(tablaPrincipal.get("codigo")));
        final TypedQuery<Reserva> consulta = entityManager.createQuery(where);
        final List<Reserva> resultados = consulta.getResultList();
        log.info("Número de resultados: {}", resultados.size());
        return resultados;
    }

}
