/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import MstCategoryDetails from './mst-category-details.vue';
import MstCategoryService from './mst-category.service';
import AlertService from '@/shared/alert/alert.service';

type MstCategoryDetailsComponentType = InstanceType<typeof MstCategoryDetails>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const mstCategorySample = { id: 123 };

describe('Component Tests', () => {
  let alertService: AlertService;

  afterEach(() => {
    vitest.resetAllMocks();
  });

  describe('MstCategory Management Detail Component', () => {
    let mstCategoryServiceStub: SinonStubbedInstance<MstCategoryService>;
    let mountOptions: MountingOptions<MstCategoryDetailsComponentType>['global'];

    beforeEach(() => {
      route = {};
      mstCategoryServiceStub = sinon.createStubInstance<MstCategoryService>(MstCategoryService);

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
          mstCategoryService: () => mstCategoryServiceStub,
        },
      };
    });

    describe('Navigate to details', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        mstCategoryServiceStub.find.resolves(mstCategorySample);
        route = {
          params: {
            mstCategoryId: '' + 123,
          },
        };
        const wrapper = shallowMount(MstCategoryDetails, { global: mountOptions });
        const comp = wrapper.vm;
        // WHEN
        await comp.$nextTick();

        // THEN
        expect(comp.mstCategory).toMatchObject(mstCategorySample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        mstCategoryServiceStub.find.resolves(mstCategorySample);
        const wrapper = shallowMount(MstCategoryDetails, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
