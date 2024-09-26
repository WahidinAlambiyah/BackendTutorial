/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import MstDistrictUpdate from './mst-district-update.vue';
import MstDistrictService from './mst-district.service';
import AlertService from '@/shared/alert/alert.service';

import MstCityService from '@/entities/mst-city/mst-city.service';

type MstDistrictUpdateComponentType = InstanceType<typeof MstDistrictUpdate>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const mstDistrictSample = { id: 123 };

describe('Component Tests', () => {
  let mountOptions: MountingOptions<MstDistrictUpdateComponentType>['global'];
  let alertService: AlertService;

  describe('MstDistrict Management Update Component', () => {
    let comp: MstDistrictUpdateComponentType;
    let mstDistrictServiceStub: SinonStubbedInstance<MstDistrictService>;

    beforeEach(() => {
      route = {};
      mstDistrictServiceStub = sinon.createStubInstance<MstDistrictService>(MstDistrictService);
      mstDistrictServiceStub.retrieve.onFirstCall().resolves(Promise.resolve([]));

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
          mstDistrictService: () => mstDistrictServiceStub,
          mstCityService: () =>
            sinon.createStubInstance<MstCityService>(MstCityService, {
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
        const wrapper = shallowMount(MstDistrictUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.mstDistrict = mstDistrictSample;
        mstDistrictServiceStub.update.resolves(mstDistrictSample);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(mstDistrictServiceStub.update.calledWith(mstDistrictSample)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        mstDistrictServiceStub.create.resolves(entity);
        const wrapper = shallowMount(MstDistrictUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.mstDistrict = entity;

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(mstDistrictServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        mstDistrictServiceStub.find.resolves(mstDistrictSample);
        mstDistrictServiceStub.retrieve.resolves([mstDistrictSample]);

        // WHEN
        route = {
          params: {
            mstDistrictId: '' + mstDistrictSample.id,
          },
        };
        const wrapper = shallowMount(MstDistrictUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(comp.mstDistrict).toMatchObject(mstDistrictSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        mstDistrictServiceStub.find.resolves(mstDistrictSample);
        const wrapper = shallowMount(MstDistrictUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
