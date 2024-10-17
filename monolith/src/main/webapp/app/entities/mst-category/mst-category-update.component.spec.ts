/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import MstCategoryUpdate from './mst-category-update.vue';
import MstCategoryService from './mst-category.service';
import AlertService from '@/shared/alert/alert.service';

type MstCategoryUpdateComponentType = InstanceType<typeof MstCategoryUpdate>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const mstCategorySample = { id: 123 };

describe('Component Tests', () => {
  let mountOptions: MountingOptions<MstCategoryUpdateComponentType>['global'];
  let alertService: AlertService;

  describe('MstCategory Management Update Component', () => {
    let comp: MstCategoryUpdateComponentType;
    let mstCategoryServiceStub: SinonStubbedInstance<MstCategoryService>;

    beforeEach(() => {
      route = {};
      mstCategoryServiceStub = sinon.createStubInstance<MstCategoryService>(MstCategoryService);
      mstCategoryServiceStub.retrieve.onFirstCall().resolves(Promise.resolve([]));

      alertService = new AlertService({
        i18n: { t: vitest.fn() } as any,
        bvToast: {
          toast: vitest.fn(),
        } as any,
      });

      mountOptions = {
        stubs: {
          'font-awesome-icon': true,
          'b-input-group': true,
          'b-input-group-prepend': true,
          'b-form-datepicker': true,
          'b-form-input': true,
        },
        provide: {
          alertService,
          mstCategoryService: () => mstCategoryServiceStub,
        },
      };
    });

    afterEach(() => {
      vitest.resetAllMocks();
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', async () => {
        // GIVEN
        const wrapper = shallowMount(MstCategoryUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.mstCategory = mstCategorySample;
        mstCategoryServiceStub.update.resolves(mstCategorySample);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(mstCategoryServiceStub.update.calledWith(mstCategorySample)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        mstCategoryServiceStub.create.resolves(entity);
        const wrapper = shallowMount(MstCategoryUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.mstCategory = entity;

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(mstCategoryServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        mstCategoryServiceStub.find.resolves(mstCategorySample);
        mstCategoryServiceStub.retrieve.resolves([mstCategorySample]);

        // WHEN
        route = {
          params: {
            mstCategoryId: '' + mstCategorySample.id,
          },
        };
        const wrapper = shallowMount(MstCategoryUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(comp.mstCategory).toMatchObject(mstCategorySample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        mstCategoryServiceStub.find.resolves(mstCategorySample);
        const wrapper = shallowMount(MstCategoryUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
