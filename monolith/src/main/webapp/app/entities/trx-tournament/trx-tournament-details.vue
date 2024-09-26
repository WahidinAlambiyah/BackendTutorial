<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="trxTournament">
        <h2 class="jh-entity-heading" data-cy="trxTournamentDetailsHeading">
          <span v-text="t$('monolithApp.trxTournament.detail.title')"></span> {{ trxTournament.id }}
        </h2>
        <dl class="row jh-entity-details">
          <dt>
            <span v-text="t$('monolithApp.trxTournament.name')"></span>
          </dt>
          <dd>
            <span>{{ trxTournament.name }}</span>
          </dd>
          <dt>
            <span v-text="t$('monolithApp.trxTournament.type')"></span>
          </dt>
          <dd>
            <span v-text="t$('monolithApp.TournamentType.' + trxTournament.type)"></span>
          </dd>
          <dt>
            <span v-text="t$('monolithApp.trxTournament.prizeAmount')"></span>
          </dt>
          <dd>
            <span>{{ trxTournament.prizeAmount }}</span>
          </dd>
          <dt>
            <span v-text="t$('monolithApp.trxTournament.startDate')"></span>
          </dt>
          <dd>
            <span v-if="trxTournament.startDate">{{ formatDateLong(trxTournament.startDate) }}</span>
          </dd>
          <dt>
            <span v-text="t$('monolithApp.trxTournament.endDate')"></span>
          </dt>
          <dd>
            <span v-if="trxTournament.endDate">{{ formatDateLong(trxTournament.endDate) }}</span>
          </dd>
          <dt>
            <span v-text="t$('monolithApp.trxTournament.location')"></span>
          </dt>
          <dd>
            <span>{{ trxTournament.location }}</span>
          </dd>
          <dt>
            <span v-text="t$('monolithApp.trxTournament.maxParticipants')"></span>
          </dt>
          <dd>
            <span>{{ trxTournament.maxParticipants }}</span>
          </dd>
          <dt>
            <span v-text="t$('monolithApp.trxTournament.status')"></span>
          </dt>
          <dd>
            <span v-text="t$('monolithApp.TournamentStatus.' + trxTournament.status)"></span>
          </dd>
          <dt>
            <span v-text="t$('monolithApp.trxTournament.event')"></span>
          </dt>
          <dd>
            <div v-if="trxTournament.event">
              <router-link :to="{ name: 'TrxEventView', params: { trxEventId: trxTournament.event.id } }">{{
                trxTournament.event.title
              }}</router-link>
            </div>
          </dd>
        </dl>
        <button type="submit" v-on:click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.back')"></span>
        </button>
        <router-link
          v-if="trxTournament.id"
          :to="{ name: 'TrxTournamentEdit', params: { trxTournamentId: trxTournament.id } }"
          custom
          v-slot="{ navigate }"
        >
          <button @click="navigate" class="btn btn-primary">
            <font-awesome-icon icon="pencil-alt"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.edit')"></span>
          </button>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./trx-tournament-details.component.ts"></script>
