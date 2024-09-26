/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import MstEmployeeDetails from './mst-employee-details.vue';
import MstEmployeeService from './mst-employee.service';
import AlertService from '@/shared/alert/alert.service';

type MstEmployeeDetailsComponentType = InstanceType<typeof MstEmployeeDetails>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const mstEmployeeSample = { id: 123 };

describe('Component Tests', () => {
  let alertService: AlertService;

  afterEach(() => {
    vitest.resetAllMocks();
  });

  describe('MstEmployee Management Detail Component', () => {
    let mstEmployeeServiceStub: SinonStubbedInstance<MstEmployeeService>;
    let mountOptions: MountingOptions<MstEmployeeDetailsComponentType>['global'];

    beforeEach(() => {
      route = {};
      mstEmployeeServiceStub = sinon.createStubInstance<MstEmployeeService>(MstEmployeeService);

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
          mstEmployeeService: () => mstEmployeeServiceStub,
        },
      };
    });

    describe('Navigate to details', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        mstEmployeeServiceStub.find.resolves(mstEmployeeSample);
        route = {
          params: {
            mstEmployeeId: '' + 123,
          },
        };
        const wrapper = shallowMount(MstEmployeeDetails, { global: mountOptions });
        const comp = wrapper.vm;
        // WHEN
        await comp.$nextTick();

        // THEN
        expect(comp.mstEmployee).toMatchObject(mstEmployeeSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        mstEmployeeServiceStub.find.resolves(mstEmployeeSample);
        const wrapper = shallowMount(MstEmployeeDetails, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
