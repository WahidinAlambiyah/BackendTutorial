/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import TrxTournamentDetails from './trx-tournament-details.vue';
import TrxTournamentService from './trx-tournament.service';
import AlertService from '@/shared/alert/alert.service';

type TrxTournamentDetailsComponentType = InstanceType<typeof TrxTournamentDetails>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const trxTournamentSample = { id: 123 };

describe('Component Tests', () => {
  let alertService: AlertService;

  afterEach(() => {
    vitest.resetAllMocks();
  });

  describe('TrxTournament Management Detail Component', () => {
    let trxTournamentServiceStub: SinonStubbedInstance<TrxTournamentService>;
    let mountOptions: MountingOptions<TrxTournamentDetailsComponentType>['global'];

    beforeEach(() => {
      route = {};
      trxTournamentServiceStub = sinon.createStubInstance<TrxTournamentService>(TrxTournamentService);

      alertService = new AlertService({
        i18n: { t: vitest.fn() } as any,
        bvToast: {
          toast: vitest.fn(),
        } as any,
      });

      mountOptions = {
        stubs: {
          'font-awesome-icon': true,
          'router-link': true,
        },
        provide: {
          alertService,
          trxTournamentService: () => trxTournamentServiceStub,
        },
      };
    });

    describe('Navigate to details', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        trxTournamentServiceStub.find.resolves(trxTournamentSample);
        route = {
          params: {
            trxTournamentId: '' + 123,
          },
        };
        const wrapper = shallowMount(TrxTournamentDetails, { global: mountOptions });
        const comp = wrapper.vm;
        // WHEN
        await comp.$nextTick();

        // THEN
        expect(comp.trxTournament).toMatchObject(trxTournamentSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        trxTournamentServiceStub.find.resolves(trxTournamentSample);
        const wrapper = shallowMount(TrxTournamentDetails, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
