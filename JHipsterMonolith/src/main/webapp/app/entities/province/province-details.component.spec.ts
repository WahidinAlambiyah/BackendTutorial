/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import ProvinceDetails from './province-details.vue';
import ProvinceService from './province.service';
import AlertService from '@/shared/alert/alert.service';

type ProvinceDetailsComponentType = InstanceType<typeof ProvinceDetails>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const provinceSample = { id: 123 };

describe('Component Tests', () => {
  let alertService: AlertService;

  afterEach(() => {
    vitest.resetAllMocks();
  });

  describe('Province Management Detail Component', () => {
    let provinceServiceStub: SinonStubbedInstance<ProvinceService>;
    let mountOptions: MountingOptions<ProvinceDetailsComponentType>['global'];

    beforeEach(() => {
      route = {};
      provinceServiceStub = sinon.createStubInstance<ProvinceService>(ProvinceService);

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
          provinceService: () => provinceServiceStub,
        },
      };
    });

    describe('Navigate to details', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        provinceServiceStub.find.resolves(provinceSample);
        route = {
          params: {
            provinceId: '' + 123,
          },
        };
        const wrapper = shallowMount(ProvinceDetails, { global: mountOptions });
        const comp = wrapper.vm;
        // WHEN
        await comp.$nextTick();

        // THEN
        expect(comp.province).toMatchObject(provinceSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        provinceServiceStub.find.resolves(provinceSample);
        const wrapper = shallowMount(ProvinceDetails, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
