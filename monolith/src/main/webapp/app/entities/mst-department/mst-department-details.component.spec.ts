/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import MstDepartmentDetails from './mst-department-details.vue';
import MstDepartmentService from './mst-department.service';
import AlertService from '@/shared/alert/alert.service';

type MstDepartmentDetailsComponentType = InstanceType<typeof MstDepartmentDetails>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const mstDepartmentSample = { id: 123 };

describe('Component Tests', () => {
  let alertService: AlertService;

  afterEach(() => {
    vitest.resetAllMocks();
  });

  describe('MstDepartment Management Detail Component', () => {
    let mstDepartmentServiceStub: SinonStubbedInstance<MstDepartmentService>;
    let mountOptions: MountingOptions<MstDepartmentDetailsComponentType>['global'];

    beforeEach(() => {
      route = {};
      mstDepartmentServiceStub = sinon.createStubInstance<MstDepartmentService>(MstDepartmentService);

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
          mstDepartmentService: () => mstDepartmentServiceStub,
        },
      };
    });

    describe('Navigate to details', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        mstDepartmentServiceStub.find.resolves(mstDepartmentSample);
        route = {
          params: {
            mstDepartmentId: '' + 123,
          },
        };
        const wrapper = shallowMount(MstDepartmentDetails, { global: mountOptions });
        const comp = wrapper.vm;
        // WHEN
        await comp.$nextTick();

        // THEN
        expect(comp.mstDepartment).toMatchObject(mstDepartmentSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        mstDepartmentServiceStub.find.resolves(mstDepartmentSample);
        const wrapper = shallowMount(MstDepartmentDetails, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
