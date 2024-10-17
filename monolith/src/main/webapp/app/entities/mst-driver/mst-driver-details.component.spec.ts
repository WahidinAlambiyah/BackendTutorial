/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import MstDriverDetails from './mst-driver-details.vue';
import MstDriverService from './mst-driver.service';
import AlertService from '@/shared/alert/alert.service';

type MstDriverDetailsComponentType = InstanceType<typeof MstDriverDetails>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const mstDriverSample = { id: 123 };

describe('Component Tests', () => {
  let alertService: AlertService;

  afterEach(() => {
    vitest.resetAllMocks();
  });

  describe('MstDriver Management Detail Component', () => {
    let mstDriverServiceStub: SinonStubbedInstance<MstDriverService>;
    let mountOptions: MountingOptions<MstDriverDetailsComponentType>['global'];

    beforeEach(() => {
      route = {};
      mstDriverServiceStub = sinon.createStubInstance<MstDriverService>(MstDriverService);

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
          mstDriverService: () => mstDriverServiceStub,
        },
      };
    });

    describe('Navigate to details', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        mstDriverServiceStub.find.resolves(mstDriverSample);
        route = {
          params: {
            mstDriverId: '' + 123,
          },
        };
        const wrapper = shallowMount(MstDriverDetails, { global: mountOptions });
        const comp = wrapper.vm;
        // WHEN
        await comp.$nextTick();

        // THEN
        expect(comp.mstDriver).toMatchObject(mstDriverSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        mstDriverServiceStub.find.resolves(mstDriverSample);
        const wrapper = shallowMount(MstDriverDetails, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
