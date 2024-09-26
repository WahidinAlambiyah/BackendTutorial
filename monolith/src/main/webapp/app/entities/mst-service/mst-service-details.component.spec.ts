/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import MstServiceDetails from './mst-service-details.vue';
import MstServiceService from './mst-service.service';
import AlertService from '@/shared/alert/alert.service';

type MstServiceDetailsComponentType = InstanceType<typeof MstServiceDetails>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const mstServiceSample = { id: 123 };

describe('Component Tests', () => {
  let alertService: AlertService;

  afterEach(() => {
    vitest.resetAllMocks();
  });

  describe('MstService Management Detail Component', () => {
    let mstServiceServiceStub: SinonStubbedInstance<MstServiceService>;
    let mountOptions: MountingOptions<MstServiceDetailsComponentType>['global'];

    beforeEach(() => {
      route = {};
      mstServiceServiceStub = sinon.createStubInstance<MstServiceService>(MstServiceService);

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
          mstServiceService: () => mstServiceServiceStub,
        },
      };
    });

    describe('Navigate to details', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        mstServiceServiceStub.find.resolves(mstServiceSample);
        route = {
          params: {
            mstServiceId: '' + 123,
          },
        };
        const wrapper = shallowMount(MstServiceDetails, { global: mountOptions });
        const comp = wrapper.vm;
        // WHEN
        await comp.$nextTick();

        // THEN
        expect(comp.mstService).toMatchObject(mstServiceSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        mstServiceServiceStub.find.resolves(mstServiceSample);
        const wrapper = shallowMount(MstServiceDetails, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
