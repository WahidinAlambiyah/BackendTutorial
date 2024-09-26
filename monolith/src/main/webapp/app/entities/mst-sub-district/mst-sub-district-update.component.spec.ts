/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import MstSubDistrictUpdate from './mst-sub-district-update.vue';
import MstSubDistrictService from './mst-sub-district.service';
import AlertService from '@/shared/alert/alert.service';

import MstDistrictService from '@/entities/mst-district/mst-district.service';

type MstSubDistrictUpdateComponentType = InstanceType<typeof MstSubDistrictUpdate>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const mstSubDistrictSample = { id: 123 };

describe('Component Tests', () => {
  let mountOptions: MountingOptions<MstSubDistrictUpdateComponentType>['global'];
  let alertService: AlertService;

  describe('MstSubDistrict Management Update Component', () => {
    let comp: MstSubDistrictUpdateComponentType;
    let mstSubDistrictServiceStub: SinonStubbedInstance<MstSubDistrictService>;

    beforeEach(() => {
      route = {};
      mstSubDistrictServiceStub = sinon.createStubInstance<MstSubDistrictService>(MstSubDistrictService);
      mstSubDistrictServiceStub.retrieve.onFirstCall().resolves(Promise.resolve([]));

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
          mstSubDistrictService: () => mstSubDistrictServiceStub,
          mstDistrictService: () =>
            sinon.createStubInstance<MstDistrictService>(MstDistrictService, {
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
        const wrapper = shallowMount(MstSubDistrictUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.mstSubDistrict = mstSubDistrictSample;
        mstSubDistrictServiceStub.update.resolves(mstSubDistrictSample);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(mstSubDistrictServiceStub.update.calledWith(mstSubDistrictSample)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        mstSubDistrictServiceStub.create.resolves(entity);
        const wrapper = shallowMount(MstSubDistrictUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.mstSubDistrict = entity;

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(mstSubDistrictServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        mstSubDistrictServiceStub.find.resolves(mstSubDistrictSample);
        mstSubDistrictServiceStub.retrieve.resolves([mstSubDistrictSample]);

        // WHEN
        route = {
          params: {
            mstSubDistrictId: '' + mstSubDistrictSample.id,
          },
        };
        const wrapper = shallowMount(MstSubDistrictUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(comp.mstSubDistrict).toMatchObject(mstSubDistrictSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        mstSubDistrictServiceStub.find.resolves(mstSubDistrictSample);
        const wrapper = shallowMount(MstSubDistrictUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
