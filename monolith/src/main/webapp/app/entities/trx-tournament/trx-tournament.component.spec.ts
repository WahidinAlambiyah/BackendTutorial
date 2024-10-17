/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';

import TrxTournament from './trx-tournament.vue';
import TrxTournamentService from './trx-tournament.service';
import AlertService from '@/shared/alert/alert.service';

type TrxTournamentComponentType = InstanceType<typeof TrxTournament>;

const bModalStub = {
  render: () => {},
  methods: {
    hide: () => {},
    show: () => {},
  },
};

describe('Component Tests', () => {
  let alertService: AlertService;

  describe('TrxTournament Management Component', () => {
    let trxTournamentServiceStub: SinonStubbedInstance<TrxTournamentService>;
    let mountOptions: MountingOptions<TrxTournamentComponentType>['global'];

    beforeEach(() => {
      trxTournamentServiceStub = sinon.createStubInstance<TrxTournamentService>(TrxTournamentService);
      trxTournamentServiceStub.retrieve.resolves({ headers: {} });

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
          trxTournamentService: () => trxTournamentServiceStub,
        },
      };
    });

    describe('Mount', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        trxTournamentServiceStub.retrieve.resolves({ headers: {}, data: [{ id: 123 }] });

        // WHEN
        const wrapper = shallowMount(TrxTournament, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(trxTournamentServiceStub.retrieve.calledOnce).toBeTruthy();
        expect(comp.trxTournaments[0]).toEqual(expect.objectContaining({ id: 123 }));
      });

      it('should calculate the sort attribute for an id', async () => {
        // WHEN
        const wrapper = shallowMount(TrxTournament, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(trxTournamentServiceStub.retrieve.lastCall.firstArg).toMatchObject({
          sort: ['id,asc'],
        });
      });
    });
    describe('Handles', () => {
      let comp: TrxTournamentComponentType;

      beforeEach(async () => {
        const wrapper = shallowMount(TrxTournament, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();
        trxTournamentServiceStub.retrieve.reset();
        trxTournamentServiceStub.retrieve.resolves({ headers: {}, data: [] });
      });

      it('should load a page', async () => {
        // GIVEN
        trxTournamentServiceStub.retrieve.resolves({ headers: {}, data: [{ id: 123 }] });

        // WHEN
        comp.page = 2;
        await comp.$nextTick();

        // THEN
        expect(trxTournamentServiceStub.retrieve.called).toBeTruthy();
        expect(comp.trxTournaments[0]).toEqual(expect.objectContaining({ id: 123 }));
      });

      it('should not load a page if the page is the same as the previous page', () => {
        // WHEN
        comp.page = 1;

        // THEN
        expect(trxTournamentServiceStub.retrieve.called).toBeFalsy();
      });

      it('should re-initialize the page', async () => {
        // GIVEN
        comp.page = 2;
        await comp.$nextTick();
        trxTournamentServiceStub.retrieve.reset();
        trxTournamentServiceStub.retrieve.resolves({ headers: {}, data: [{ id: 123 }] });

        // WHEN
        comp.clear();
        await comp.$nextTick();

        // THEN
        expect(comp.page).toEqual(1);
        expect(trxTournamentServiceStub.retrieve.callCount).toEqual(1);
        expect(comp.trxTournaments[0]).toEqual(expect.objectContaining({ id: 123 }));
      });

      it('should calculate the sort attribute for a non-id attribute', async () => {
        // WHEN
        comp.propOrder = 'name';
        await comp.$nextTick();

        // THEN
        expect(trxTournamentServiceStub.retrieve.lastCall.firstArg).toMatchObject({
          sort: ['name,asc', 'id'],
        });
      });

      it('Should call delete service on confirmDelete', async () => {
        // GIVEN
        trxTournamentServiceStub.delete.resolves({});

        // WHEN
        comp.prepareRemove({ id: 123 });

        comp.removeTrxTournament();
        await comp.$nextTick(); // clear components

        // THEN
        expect(trxTournamentServiceStub.delete.called).toBeTruthy();

        // THEN
        await comp.$nextTick(); // handle component clear watch
        expect(trxTournamentServiceStub.retrieve.callCount).toEqual(1);
      });
    });
  });
});
