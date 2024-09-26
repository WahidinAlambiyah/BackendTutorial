/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import MstRegionDetails from './mst-region-details.vue';
import MstRegionService from './mst-region.service';
import AlertService from '@/shared/alert/alert.service';

type MstRegionDetailsComponentType = InstanceType<typeof MstRegionDetails>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const mstRegionSample = { id: 123 };

describe('Component Tests', () => {
  let alertService: AlertService;

  afterEach(() => {
    vitest.resetAllMocks();
  });

  describe('MstRegion Management Detail Component', () => {
    let mstRegionServiceStub: SinonStubbedInstance<MstRegionService>;
    let mountOptions: MountingOptions<MstRegionDetailsComponentType>['global'];

    beforeEach(() => {
      route = {};
      mstRegionServiceStub = sinon.createStubInstance<MstRegionService>(MstRegionService);

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
          mstRegionService: () => mstRegionServiceStub,
        },
      };
    });

    describe('Navigate to details', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        mstRegionServiceStub.find.resolves(mstRegionSample);
        route = {
          params: {
            mstRegionId: '' + 123,
          },
        };
        const wrapper = shallowMount(MstRegionDetails, { global: mountOptions });
        const comp = wrapper.vm;
        // WHEN
        await comp.$nextTick();

        // THEN
        expect(comp.mstRegion).toMatchObject(mstRegionSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        mstRegionServiceStub.find.resolves(mstRegionSample);
        const wrapper = shallowMount(MstRegionDetails, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
