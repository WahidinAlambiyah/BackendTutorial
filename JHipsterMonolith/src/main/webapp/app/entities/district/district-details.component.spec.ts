/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import DistrictDetails from './district-details.vue';
import DistrictService from './district.service';
import AlertService from '@/shared/alert/alert.service';

type DistrictDetailsComponentType = InstanceType<typeof DistrictDetails>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const districtSample = { id: 123 };

describe('Component Tests', () => {
  let alertService: AlertService;

  afterEach(() => {
    vitest.resetAllMocks();
  });

  describe('District Management Detail Component', () => {
    let districtServiceStub: SinonStubbedInstance<DistrictService>;
    let mountOptions: MountingOptions<DistrictDetailsComponentType>['global'];

    beforeEach(() => {
      route = {};
      districtServiceStub = sinon.createStubInstance<DistrictService>(DistrictService);

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
          districtService: () => districtServiceStub,
        },
      };
    });

    describe('Navigate to details', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        districtServiceStub.find.resolves(districtSample);
        route = {
          params: {
            districtId: '' + 123,
          },
        };
        const wrapper = shallowMount(DistrictDetails, { global: mountOptions });
        const comp = wrapper.vm;
        // WHEN
        await comp.$nextTick();

        // THEN
        expect(comp.district).toMatchObject(districtSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        districtServiceStub.find.resolves(districtSample);
        const wrapper = shallowMount(DistrictDetails, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
