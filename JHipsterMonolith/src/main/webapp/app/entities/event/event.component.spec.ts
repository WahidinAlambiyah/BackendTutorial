/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';

import Event from './event.vue';
import EventService from './event.service';
import AlertService from '@/shared/alert/alert.service';

type EventComponentType = InstanceType<typeof Event>;

const bModalStub = {
  render: () => {},
  methods: {
    hide: () => {},
    show: () => {},
  },
};

describe('Component Tests', () => {
  let alertService: AlertService;

  describe('Event Management Component', () => {
    let eventServiceStub: SinonStubbedInstance<EventService>;
    let mountOptions: MountingOptions<EventComponentType>['global'];

    beforeEach(() => {
      eventServiceStub = sinon.createStubInstance<EventService>(EventService);
      eventServiceStub.retrieve.resolves({ headers: {} });

      alertService = new AlertService({
        i18n: { t: vitest.fn() } as any,
        bvToast: {
          toast: vitest.fn(),
        } as any,
      });

      mountOptions = {
        stubs: {
          bModal: bModalStub as any,
          'font-awesome-icon': true,
          'b-badge': true,
          'b-button': true,
          'router-link': true,
        },
        directives: {
          'b-modal': {},
        },
        provide: {
          alertService,
          eventService: () => eventServiceStub,
        },
      };
    });

    describe('Mount', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        eventServiceStub.retrieve.resolves({ headers: {}, data: [{ id: 123 }] });

        // WHEN
        const wrapper = shallowMount(Event, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(eventServiceStub.retrieve.calledOnce).toBeTruthy();
        expect(comp.events[0]).toEqual(expect.objectContaining({ id: 123 }));
      });
    });
    describe('Handles', () => {
      let comp: EventComponentType;

      beforeEach(async () => {
        const wrapper = shallowMount(Event, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();
        eventServiceStub.retrieve.reset();
        eventServiceStub.retrieve.resolves({ headers: {}, data: [] });
      });

      it('Should call delete service on confirmDelete', async () => {
        // GIVEN
        eventServiceStub.delete.resolves({});

        // WHEN
        comp.prepareRemove({ id: 123 });

        comp.removeEvent();
        await comp.$nextTick(); // clear components

        // THEN
        expect(eventServiceStub.delete.called).toBeTruthy();

        // THEN
        await comp.$nextTick(); // handle component clear watch
        expect(eventServiceStub.retrieve.callCount).toEqual(1);
      });
    });
  });
});
