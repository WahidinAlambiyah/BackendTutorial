/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import dayjs from 'dayjs';
import TournamentUpdate from './tournament-update.vue';
import TournamentService from './tournament.service';
import { DATE_TIME_LONG_FORMAT } from '@/shared/composables/date-format';
import AlertService from '@/shared/alert/alert.service';

import EventService from '@/entities/event/event.service';

type TournamentUpdateComponentType = InstanceType<typeof TournamentUpdate>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const tournamentSample = { id: 123 };

describe('Component Tests', () => {
  let mountOptions: MountingOptions<TournamentUpdateComponentType>['global'];
  let alertService: AlertService;

  describe('Tournament Management Update Component', () => {
    let comp: TournamentUpdateComponentType;
    let tournamentServiceStub: SinonStubbedInstance<TournamentService>;

    beforeEach(() => {
      route = {};
      tournamentServiceStub = sinon.createStubInstance<TournamentService>(TournamentService);
      tournamentServiceStub.retrieve.onFirstCall().resolves(Promise.resolve([]));

      alertService = new AlertService({
        i18n: { t: vitest.fn() } as any,
        bvToast: {
          toast: vitest.fn(),
        } as any,
      });

      mountOptions = {
        stubs: {
          'font-awesome-icon': true,
          'b-input-group': true,
          'b-input-group-prepend': true,
          'b-form-datepicker': true,
          'b-form-input': true,
        },
        provide: {
          alertService,
          tournamentService: () => tournamentServiceStub,
          eventService: () =>
            sinon.createStubInstance<EventService>(EventService, {
              retrieve: sinon.stub().resolves({}),
            } as any),
        },
      };
    });

    afterEach(() => {
      vitest.resetAllMocks();
    });

    describe('load', () => {
      beforeEach(() => {
        const wrapper = shallowMount(TournamentUpdate, { global: mountOptions });
        comp = wrapper.vm;
      });
      it('Should convert date from string', () => {
        // GIVEN
        const date = new Date('2019-10-15T11:42:02Z');

        // WHEN
        const convertedDate = comp.convertDateTimeFromServer(date);

        // THEN
        expect(convertedDate).toEqual(dayjs(date).format(DATE_TIME_LONG_FORMAT));
      });

      it('Should not convert date if date is not present', () => {
        expect(comp.convertDateTimeFromServer(null)).toBeNull();
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', async () => {
        // GIVEN
        const wrapper = shallowMount(TournamentUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.tournament = tournamentSample;
        tournamentServiceStub.update.resolves(tournamentSample);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(tournamentServiceStub.update.calledWith(tournamentSample)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        tournamentServiceStub.create.resolves(entity);
        const wrapper = shallowMount(TournamentUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.tournament = entity;

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(tournamentServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        tournamentServiceStub.find.resolves(tournamentSample);
        tournamentServiceStub.retrieve.resolves([tournamentSample]);

        // WHEN
        route = {
          params: {
            tournamentId: '' + tournamentSample.id,
          },
        };
        const wrapper = shallowMount(TournamentUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(comp.tournament).toMatchObject(tournamentSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        tournamentServiceStub.find.resolves(tournamentSample);
        const wrapper = shallowMount(TournamentUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
