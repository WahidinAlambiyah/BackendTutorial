/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import dayjs from 'dayjs';
import TrxTournamentUpdate from './trx-tournament-update.vue';
import TrxTournamentService from './trx-tournament.service';
import { DATE_TIME_LONG_FORMAT } from '@/shared/composables/date-format';
import AlertService from '@/shared/alert/alert.service';

import TrxEventService from '@/entities/trx-event/trx-event.service';

type TrxTournamentUpdateComponentType = InstanceType<typeof TrxTournamentUpdate>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const trxTournamentSample = { id: 123 };

describe('Component Tests', () => {
  let mountOptions: MountingOptions<TrxTournamentUpdateComponentType>['global'];
  let alertService: AlertService;

  describe('TrxTournament Management Update Component', () => {
    let comp: TrxTournamentUpdateComponentType;
    let trxTournamentServiceStub: SinonStubbedInstance<TrxTournamentService>;

    beforeEach(() => {
      route = {};
      trxTournamentServiceStub = sinon.createStubInstance<TrxTournamentService>(TrxTournamentService);
      trxTournamentServiceStub.retrieve.onFirstCall().resolves(Promise.resolve([]));

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
          trxTournamentService: () => trxTournamentServiceStub,
          trxEventService: () =>
            sinon.createStubInstance<TrxEventService>(TrxEventService, {
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
        const wrapper = shallowMount(TrxTournamentUpdate, { global: mountOptions });
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
        const wrapper = shallowMount(TrxTournamentUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.trxTournament = trxTournamentSample;
        trxTournamentServiceStub.update.resolves(trxTournamentSample);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(trxTournamentServiceStub.update.calledWith(trxTournamentSample)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        trxTournamentServiceStub.create.resolves(entity);
        const wrapper = shallowMount(TrxTournamentUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.trxTournament = entity;

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(trxTournamentServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        trxTournamentServiceStub.find.resolves(trxTournamentSample);
        trxTournamentServiceStub.retrieve.resolves([trxTournamentSample]);

        // WHEN
        route = {
          params: {
            trxTournamentId: '' + trxTournamentSample.id,
          },
        };
        const wrapper = shallowMount(TrxTournamentUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(comp.trxTournament).toMatchObject(trxTournamentSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        trxTournamentServiceStub.find.resolves(trxTournamentSample);
        const wrapper = shallowMount(TrxTournamentUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
