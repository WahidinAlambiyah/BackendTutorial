import { Authority } from '@/shared/security/authority';
/* tslint:disable */
// prettier-ignore
const Entities = () => import('@/entities/entities.vue');

const Event = () => import('@/entities/event/event.vue');
const EventUpdate = () => import('@/entities/event/event-update.vue');
const EventDetails = () => import('@/entities/event/event-details.vue');

const Tournament = () => import('@/entities/tournament/tournament.vue');
const TournamentUpdate = () => import('@/entities/tournament/tournament-update.vue');
const TournamentDetails = () => import('@/entities/tournament/tournament-details.vue');

const Service = () => import('@/entities/service/service.vue');
const ServiceUpdate = () => import('@/entities/service/service-update.vue');
const ServiceDetails = () => import('@/entities/service/service-details.vue');

const Testimonial = () => import('@/entities/testimonial/testimonial.vue');
const TestimonialUpdate = () => import('@/entities/testimonial/testimonial-update.vue');
const TestimonialDetails = () => import('@/entities/testimonial/testimonial-details.vue');

// jhipster-needle-add-entity-to-router-import - JHipster will import entities to the router here

export default {
  path: '/',
  component: Entities,
  children: [
    {
      path: 'event',
      name: 'Event',
      component: Event,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'event/new',
      name: 'EventCreate',
      component: EventUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'event/:eventId/edit',
      name: 'EventEdit',
      component: EventUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'event/:eventId/view',
      name: 'EventView',
      component: EventDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'tournament',
      name: 'Tournament',
      component: Tournament,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'tournament/new',
      name: 'TournamentCreate',
      component: TournamentUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'tournament/:tournamentId/edit',
      name: 'TournamentEdit',
      component: TournamentUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'tournament/:tournamentId/view',
      name: 'TournamentView',
      component: TournamentDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'service',
      name: 'Service',
      component: Service,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'service/new',
      name: 'ServiceCreate',
      component: ServiceUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'service/:serviceId/edit',
      name: 'ServiceEdit',
      component: ServiceUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'service/:serviceId/view',
      name: 'ServiceView',
      component: ServiceDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'testimonial',
      name: 'Testimonial',
      component: Testimonial,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'testimonial/new',
      name: 'TestimonialCreate',
      component: TestimonialUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'testimonial/:testimonialId/edit',
      name: 'TestimonialEdit',
      component: TestimonialUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'testimonial/:testimonialId/view',
      name: 'TestimonialView',
      component: TestimonialDetails,
      meta: { authorities: [Authority.USER] },
    },
    // jhipster-needle-add-entity-to-router - JHipster will add entities to the router here
  ],
};
