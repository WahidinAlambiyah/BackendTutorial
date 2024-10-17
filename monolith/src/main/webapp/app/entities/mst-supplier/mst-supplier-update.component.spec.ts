/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import MstSupplierUpdate from './mst-supplier-update.vue';
import MstSupplierService from './mst-supplier.service';
import AlertService from '@/shared/alert/alert.service';

type MstSupplierUpdateComponentType = InstanceType<typeof MstSupplierUpdate>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const mstSupplierSample = { id: 123 };

describe('Component Tests', () => {
  let mountOptions: MountingOptions<MstSupplierUpdateComponentType>['global'];
  let alertService: AlertService;

  describe('MstSupplier Management Update Component', () => {
    let comp: MstSupplierUpdateComponentType;
    let mstSupplierServiceStub: SinonStubbedInstance<MstSupplierService>;

    beforeEach(() => {
      route = {};
      mstSupplierServiceStub = sinon.createStubInstance<MstSupplierService>(MstSupplierService);
      mstSupplierServiceStub.retrieve.onFirstCall().resolves(Promise.resolve([]));

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
          mstSupplierService: () => mstSupplierServiceStub,
        },
      };
    });

    afterEach(() => {
      vitest.resetAllMocks();
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', async () => {
        // GIVEN
        const wrapper = shallowMount(MstSupplierUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.mstSupplier = mstSupplierSample;
        mstSupplierServiceStub.update.resolves(mstSupplierSample);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(mstSupplierServiceStub.update.calledWith(mstSupplierSample)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        mstSupplierServiceStub.create.resolves(entity);
        const wrapper = shallowMount(MstSupplierUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.mstSupplier = entity;

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(mstSupplierServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        mstSupplierServiceStub.find.resolves(mstSupplierSample);
        mstSupplierServiceStub.retrieve.resolves([mstSupplierSample]);

        // WHEN
        route = {
          params: {
            mstSupplierId: '' + mstSupplierSample.id,
          },
        };
        const wrapper = shallowMount(MstSupplierUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(comp.mstSupplier).toMatchObject(mstSupplierSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        mstSupplierServiceStub.find.resolves(mstSupplierSample);
        const wrapper = shallowMount(MstSupplierUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
