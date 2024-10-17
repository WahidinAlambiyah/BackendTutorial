/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import MstLoyaltyProgramDetails from './mst-loyalty-program-details.vue';
import MstLoyaltyProgramService from './mst-loyalty-program.service';
import AlertService from '@/shared/alert/alert.service';

type MstLoyaltyProgramDetailsComponentType = InstanceType<typeof MstLoyaltyProgramDetails>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const mstLoyaltyProgramSample = { id: 123 };

describe('Component Tests', () => {
  let alertService: AlertService;

  afterEach(() => {
    vitest.resetAllMocks();
  });

  describe('MstLoyaltyProgram Management Detail Component', () => {
    let mstLoyaltyProgramServiceStub: SinonStubbedInstance<MstLoyaltyProgramService>;
    let mountOptions: MountingOptions<MstLoyaltyProgramDetailsComponentType>['global'];

    beforeEach(() => {
      route = {};
      mstLoyaltyProgramServiceStub = sinon.createStubInstance<MstLoyaltyProgramService>(MstLoyaltyProgramService);

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
          mstLoyaltyProgramService: () => mstLoyaltyProgramServiceStub,
        },
      };
    });

    describe('Navigate to details', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        mstLoyaltyProgramServiceStub.find.resolves(mstLoyaltyProgramSample);
        route = {
          params: {
            mstLoyaltyProgramId: '' + 123,
          },
        };
        const wrapper = shallowMount(MstLoyaltyProgramDetails, { global: mountOptions });
        const comp = wrapper.vm;
        // WHEN
        await comp.$nextTick();

        // THEN
        expect(comp.mstLoyaltyProgram).toMatchObject(mstLoyaltyProgramSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        mstLoyaltyProgramServiceStub.find.resolves(mstLoyaltyProgramSample);
        const wrapper = shallowMount(MstLoyaltyProgramDetails, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
