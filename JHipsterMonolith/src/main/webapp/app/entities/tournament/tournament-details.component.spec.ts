/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import TournamentDetails from './tournament-details.vue';
import TournamentService from './tournament.service';
import AlertService from '@/shared/alert/alert.service';

type TournamentDetailsComponentType = InstanceType<typeof TournamentDetails>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const tournamentSample = { id: 123 };

describe('Component Tests', () => {
  let alertService: AlertService;

  afterEach(() => {
    vitest.resetAllMocks();
  });

  describe('Tournament Management Detail Component', () => {
    let tournamentServiceStub: SinonStubbedInstance<TournamentService>;
    let mountOptions: MountingOptions<TournamentDetailsComponentType>['global'];

    beforeEach(() => {
      route = {};
      tournamentServiceStub = sinon.createStubInstance<TournamentService>(TournamentService);

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
          tournamentService: () => tournamentServiceStub,
        },
      };
    });

    describe('Navigate to details', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        tournamentServiceStub.find.resolves(tournamentSample);
        route = {
          params: {
            tournamentId: '' + 123,
          },
        };
        const wrapper = shallowMount(TournamentDetails, { global: mountOptions });
        const comp = wrapper.vm;
        // WHEN
        await comp.$nextTick();

        // THEN
        expect(comp.tournament).toMatchObject(tournamentSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        tournamentServiceStub.find.resolves(tournamentSample);
        const wrapper = shallowMount(TournamentDetails, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
