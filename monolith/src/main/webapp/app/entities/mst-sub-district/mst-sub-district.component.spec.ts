/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';

import MstSubDistrict from './mst-sub-district.vue';
import MstSubDistrictService from './mst-sub-district.service';
import AlertService from '@/shared/alert/alert.service';

type MstSubDistrictComponentType = InstanceType<typeof MstSubDistrict>;

const bModalStub = {
  render: () => {},
  methods: {
    hide: () => {},
    show: () => {},
  },
};

describe('Component Tests', () => {
  let alertService: AlertService;

  describe('MstSubDistrict Management Component', () => {
    let mstSubDistrictServiceStub: SinonStubbedInstance<MstSubDistrictService>;
    let mountOptions: MountingOptions<MstSubDistrictComponentType>['global'];

    beforeEach(() => {
      mstSubDistrictServiceStub = sinon.createStubInstance<MstSubDistrictService>(MstSubDistrictService);
      mstSubDistrictServiceStub.retrieve.resolves({ headers: {} });

      alertService = new AlertService({
        i18n: { t: vitest.fn() } as any,
        bvToast: {
          toast: vitest.fn(),
        } as any,
      });

      mountOptions = {
        stubs: {
          jhiItemCount: true,
          bPagination: true,
          bModal: bModalStub as any,
          'font-awesome-icon': true,
          'b-badge': true,
          'jhi-sort-indicator': true,
          'b-button': true,
          'router-link': true,
        },
        directives: {
          'b-modal': {},
        },
        provide: {
          alertService,
          mstSubDistrictService: () => mstSubDistrictServiceStub,
        },
      };
    });

    describe('Mount', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        mstSubDistrictServiceStub.retrieve.resolves({ headers: {}, data: [{ id: 123 }] });

        // WHEN
        const wrapper = shallowMount(MstSubDistrict, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(mstSubDistrictServiceStub.retrieve.calledOnce).toBeTruthy();
        expect(comp.mstSubDistricts[0]).toEqual(expect.objectContaining({ id: 123 }));
      });

      it('should calculate the sort attribute for an id', async () => {
        // WHEN
        const wrapper = shallowMount(MstSubDistrict, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(mstSubDistrictServiceStub.retrieve.lastCall.firstArg).toMatchObject({
          sort: ['id,asc'],
        });
      });
    });
    describe('Handles', () => {
      let comp: MstSubDistrictComponentType;

      beforeEach(async () => {
        const wrapper = shallowMount(MstSubDistrict, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();
        mstSubDistrictServiceStub.retrieve.reset();
        mstSubDistrictServiceStub.retrieve.resolves({ headers: {}, data: [] });
      });

      it('should load a page', async () => {
        // GIVEN
        mstSubDistrictServiceStub.retrieve.resolves({ headers: {}, data: [{ id: 123 }] });

        // WHEN
        comp.page = 2;
        await comp.$nextTick();

        // THEN
        expect(mstSubDistrictServiceStub.retrieve.called).toBeTruthy();
        expect(comp.mstSubDistricts[0]).toEqual(expect.objectContaining({ id: 123 }));
      });

      it('should not load a page if the page is the same as the previous page', () => {
        // WHEN
        comp.page = 1;

        // THEN
        expect(mstSubDistrictServiceStub.retrieve.called).toBeFalsy();
      });

      it('should re-initialize the page', async () => {
        // GIVEN
        comp.page = 2;
        await comp.$nextTick();
        mstSubDistrictServiceStub.retrieve.reset();
        mstSubDistrictServiceStub.retrieve.resolves({ headers: {}, data: [{ id: 123 }] });

        // WHEN
        comp.clear();
        await comp.$nextTick();

        // THEN
        expect(comp.page).toEqual(1);
        expect(mstSubDistrictServiceStub.retrieve.callCount).toEqual(1);
        expect(comp.mstSubDistricts[0]).toEqual(expect.objectContaining({ id: 123 }));
      });

      it('should calculate the sort attribute for a non-id attribute', async () => {
        // WHEN
        comp.propOrder = 'name';
        await comp.$nextTick();

        // THEN
        expect(mstSubDistrictServiceStub.retrieve.lastCall.firstArg).toMatchObject({
          sort: ['name,asc', 'id'],
        });
      });

      it('Should call delete service on confirmDelete', async () => {
        // GIVEN
        mstSubDistrictServiceStub.delete.resolves({});

        // WHEN
        comp.prepareRemove({ id: 123 });

        comp.removeMstSubDistrict();
        await comp.$nextTick(); // clear components

        // THEN
        expect(mstSubDistrictServiceStub.delete.called).toBeTruthy();

        // THEN
        await comp.$nextTick(); // handle component clear watch
        expect(mstSubDistrictServiceStub.retrieve.callCount).toEqual(1);
      });
    });
  });
});
