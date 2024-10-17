/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import MstProductUpdate from './mst-product-update.vue';
import MstProductService from './mst-product.service';
import AlertService from '@/shared/alert/alert.service';

import MstCategoryService from '@/entities/mst-category/mst-category.service';
import MstBrandService from '@/entities/mst-brand/mst-brand.service';
import MstSupplierService from '@/entities/mst-supplier/mst-supplier.service';

type MstProductUpdateComponentType = InstanceType<typeof MstProductUpdate>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const mstProductSample = { id: 123 };

describe('Component Tests', () => {
  let mountOptions: MountingOptions<MstProductUpdateComponentType>['global'];
  let alertService: AlertService;

  describe('MstProduct Management Update Component', () => {
    let comp: MstProductUpdateComponentType;
    let mstProductServiceStub: SinonStubbedInstance<MstProductService>;

    beforeEach(() => {
      route = {};
      mstProductServiceStub = sinon.createStubInstance<MstProductService>(MstProductService);
      mstProductServiceStub.retrieve.onFirstCall().resolves(Promise.resolve([]));

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
          mstProductService: () => mstProductServiceStub,
          mstCategoryService: () =>
            sinon.createStubInstance<MstCategoryService>(MstCategoryService, {
              retrieve: sinon.stub().resolves({}),
            } as any),
          mstBrandService: () =>
            sinon.createStubInstance<MstBrandService>(MstBrandService, {
              retrieve: sinon.stub().resolves({}),
            } as any),
          mstSupplierService: () =>
            sinon.createStubInstance<MstSupplierService>(MstSupplierService, {
              retrieve: sinon.stub().resolves({}),
            } as any),
        },
      };
    });

    afterEach(() => {
      vitest.resetAllMocks();
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', async () => {
        // GIVEN
        const wrapper = shallowMount(MstProductUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.mstProduct = mstProductSample;
        mstProductServiceStub.update.resolves(mstProductSample);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(mstProductServiceStub.update.calledWith(mstProductSample)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        mstProductServiceStub.create.resolves(entity);
        const wrapper = shallowMount(MstProductUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.mstProduct = entity;

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(mstProductServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        mstProductServiceStub.find.resolves(mstProductSample);
        mstProductServiceStub.retrieve.resolves([mstProductSample]);

        // WHEN
        route = {
          params: {
            mstProductId: '' + mstProductSample.id,
          },
        };
        const wrapper = shallowMount(MstProductUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(comp.mstProduct).toMatchObject(mstProductSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        mstProductServiceStub.find.resolves(mstProductSample);
        const wrapper = shallowMount(MstProductUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
