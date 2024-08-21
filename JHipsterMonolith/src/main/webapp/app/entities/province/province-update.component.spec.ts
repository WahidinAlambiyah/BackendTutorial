/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import ProvinceUpdate from './province-update.vue';
import ProvinceService from './province.service';
import AlertService from '@/shared/alert/alert.service';

import CountryService from '@/entities/country/country.service';

type ProvinceUpdateComponentType = InstanceType<typeof ProvinceUpdate>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const provinceSample = { id: 123 };

describe('Component Tests', () => {
  let mountOptions: MountingOptions<ProvinceUpdateComponentType>['global'];
  let alertService: AlertService;

  describe('Province Management Update Component', () => {
    let comp: ProvinceUpdateComponentType;
    let provinceServiceStub: SinonStubbedInstance<ProvinceService>;

    beforeEach(() => {
      route = {};
      provinceServiceStub = sinon.createStubInstance<ProvinceService>(ProvinceService);
      provinceServiceStub.retrieve.onFirstCall().resolves(Promise.resolve([]));

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
          provinceService: () => provinceServiceStub,
          countryService: () =>
            sinon.createStubInstance<CountryService>(CountryService, {
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
        const wrapper = shallowMount(ProvinceUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.province = provinceSample;
        provinceServiceStub.update.resolves(provinceSample);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(provinceServiceStub.update.calledWith(provinceSample)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        provinceServiceStub.create.resolves(entity);
        const wrapper = shallowMount(ProvinceUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.province = entity;

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(provinceServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        provinceServiceStub.find.resolves(provinceSample);
        provinceServiceStub.retrieve.resolves([provinceSample]);

        // WHEN
        route = {
          params: {
            provinceId: '' + provinceSample.id,
          },
        };
        const wrapper = shallowMount(ProvinceUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(comp.province).toMatchObject(provinceSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        provinceServiceStub.find.resolves(provinceSample);
        const wrapper = shallowMount(ProvinceUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
