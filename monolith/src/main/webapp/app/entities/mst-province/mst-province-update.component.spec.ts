/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import MstProvinceUpdate from './mst-province-update.vue';
import MstProvinceService from './mst-province.service';
import AlertService from '@/shared/alert/alert.service';

import MstCountryService from '@/entities/mst-country/mst-country.service';

type MstProvinceUpdateComponentType = InstanceType<typeof MstProvinceUpdate>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const mstProvinceSample = { id: 123 };

describe('Component Tests', () => {
  let mountOptions: MountingOptions<MstProvinceUpdateComponentType>['global'];
  let alertService: AlertService;

  describe('MstProvince Management Update Component', () => {
    let comp: MstProvinceUpdateComponentType;
    let mstProvinceServiceStub: SinonStubbedInstance<MstProvinceService>;

    beforeEach(() => {
      route = {};
      mstProvinceServiceStub = sinon.createStubInstance<MstProvinceService>(MstProvinceService);
      mstProvinceServiceStub.retrieve.onFirstCall().resolves(Promise.resolve([]));

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
          mstProvinceService: () => mstProvinceServiceStub,
          mstCountryService: () =>
            sinon.createStubInstance<MstCountryService>(MstCountryService, {
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
        const wrapper = shallowMount(MstProvinceUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.mstProvince = mstProvinceSample;
        mstProvinceServiceStub.update.resolves(mstProvinceSample);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(mstProvinceServiceStub.update.calledWith(mstProvinceSample)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        mstProvinceServiceStub.create.resolves(entity);
        const wrapper = shallowMount(MstProvinceUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.mstProvince = entity;

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(mstProvinceServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        mstProvinceServiceStub.find.resolves(mstProvinceSample);
        mstProvinceServiceStub.retrieve.resolves([mstProvinceSample]);

        // WHEN
        route = {
          params: {
            mstProvinceId: '' + mstProvinceSample.id,
          },
        };
        const wrapper = shallowMount(MstProvinceUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(comp.mstProvince).toMatchObject(mstProvinceSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        mstProvinceServiceStub.find.resolves(mstProvinceSample);
        const wrapper = shallowMount(MstProvinceUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
