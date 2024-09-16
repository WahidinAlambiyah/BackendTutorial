import { defineComponent, provide } from 'vue';

import EventService from './event/event.service';
import TournamentService from './tournament/tournament.service';
import ServiceService from './service/service.service';
import TestimonialService from './testimonial/testimonial.service';
import UserService from '@/entities/user/user.service';
// jhipster-needle-add-entity-service-to-entities-component-import - JHipster will import entities services here

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'Entities',
  setup() {
    provide('userService', () => new UserService());
    provide('eventService', () => new EventService());
    provide('tournamentService', () => new TournamentService());
    provide('serviceService', () => new ServiceService());
    provide('testimonialService', () => new TestimonialService());
    // jhipster-needle-add-entity-service-to-entities-component - JHipster will import entities services here
  },
});
