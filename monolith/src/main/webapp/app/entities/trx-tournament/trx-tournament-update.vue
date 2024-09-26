<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" novalidate v-on:submit.prevent="save()">
        <h2
          id="monolithApp.trxTournament.home.createOrEditLabel"
          data-cy="TrxTournamentCreateUpdateHeading"
          v-text="t$('monolithApp.trxTournament.home.createOrEditLabel')"
        ></h2>
        <div>
          <div class="form-group" v-if="trxTournament.id">
            <label for="id" v-text="t$('global.field.id')"></label>
            <input type="text" class="form-control" id="id" name="id" v-model="trxTournament.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('monolithApp.trxTournament.name')" for="trx-tournament-name"></label>
            <input
              type="text"
              class="form-control"
              name="name"
              id="trx-tournament-name"
              data-cy="name"
              :class="{ valid: !v$.name.$invalid, invalid: v$.name.$invalid }"
              v-model="v$.name.$model"
              required
            />
            <div v-if="v$.name.$anyDirty && v$.name.$invalid">
              <small class="form-text text-danger" v-for="error of v$.name.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('monolithApp.trxTournament.type')" for="trx-tournament-type"></label>
            <select
              class="form-control"
              name="type"
              :class="{ valid: !v$.type.$invalid, invalid: v$.type.$invalid }"
              v-model="v$.type.$model"
              id="trx-tournament-type"
              data-cy="type"
            >
              <option
                v-for="tournamentType in tournamentTypeValues"
                :key="tournamentType"
                v-bind:value="tournamentType"
                v-bind:label="t$('monolithApp.TournamentType.' + tournamentType)"
              >
                {{ tournamentType }}
              </option>
            </select>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('monolithApp.trxTournament.prizeAmount')" for="trx-tournament-prizeAmount"></label>
            <input
              type="number"
              class="form-control"
              name="prizeAmount"
              id="trx-tournament-prizeAmount"
              data-cy="prizeAmount"
              :class="{ valid: !v$.prizeAmount.$invalid, invalid: v$.prizeAmount.$invalid }"
              v-model.number="v$.prizeAmount.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('monolithApp.trxTournament.startDate')" for="trx-tournament-startDate"></label>
            <div class="d-flex">
              <input
                id="trx-tournament-startDate"
                data-cy="startDate"
                type="datetime-local"
                class="form-control"
                name="startDate"
                :class="{ valid: !v$.startDate.$invalid, invalid: v$.startDate.$invalid }"
                required
                :value="convertDateTimeFromServer(v$.startDate.$model)"
                @change="updateInstantField('startDate', $event)"
              />
            </div>
            <div v-if="v$.startDate.$anyDirty && v$.startDate.$invalid">
              <small class="form-text text-danger" v-for="error of v$.startDate.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('monolithApp.trxTournament.endDate')" for="trx-tournament-endDate"></label>
            <div class="d-flex">
              <input
                id="trx-tournament-endDate"
                data-cy="endDate"
                type="datetime-local"
                class="form-control"
                name="endDate"
                :class="{ valid: !v$.endDate.$invalid, invalid: v$.endDate.$invalid }"
                required
                :value="convertDateTimeFromServer(v$.endDate.$model)"
                @change="updateInstantField('endDate', $event)"
              />
            </div>
            <div v-if="v$.endDate.$anyDirty && v$.endDate.$invalid">
              <small class="form-text text-danger" v-for="error of v$.endDate.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('monolithApp.trxTournament.location')" for="trx-tournament-location"></label>
            <input
              type="text"
              class="form-control"
              name="location"
              id="trx-tournament-location"
              data-cy="location"
              :class="{ valid: !v$.location.$invalid, invalid: v$.location.$invalid }"
              v-model="v$.location.$model"
            />
          </div>
          <div class="form-group">
            <label
              class="form-control-label"
              v-text="t$('monolithApp.trxTournament.maxParticipants')"
              for="trx-tournament-maxParticipants"
            ></label>
            <input
              type="number"
              class="form-control"
              name="maxParticipants"
              id="trx-tournament-maxParticipants"
              data-cy="maxParticipants"
              :class="{ valid: !v$.maxParticipants.$invalid, invalid: v$.maxParticipants.$invalid }"
              v-model.number="v$.maxParticipants.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('monolithApp.trxTournament.status')" for="trx-tournament-status"></label>
            <select
              class="form-control"
              name="status"
              :class="{ valid: !v$.status.$invalid, invalid: v$.status.$invalid }"
              v-model="v$.status.$model"
              id="trx-tournament-status"
              data-cy="status"
            >
              <option
                v-for="tournamentStatus in tournamentStatusValues"
                :key="tournamentStatus"
                v-bind:value="tournamentStatus"
                v-bind:label="t$('monolithApp.TournamentStatus.' + tournamentStatus)"
              >
                {{ tournamentStatus }}
              </option>
            </select>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('monolithApp.trxTournament.event')" for="trx-tournament-event"></label>
            <select class="form-control" id="trx-tournament-event" data-cy="event" name="event" v-model="trxTournament.event">
              <option v-bind:value="null"></option>
              <option
                v-bind:value="trxTournament.event && trxEventOption.id === trxTournament.event.id ? trxTournament.event : trxEventOption"
                v-for="trxEventOption in trxEvents"
                :key="trxEventOption.id"
              >
                {{ trxEventOption.title }}
              </option>
            </select>
          </div>
        </div>
        <div>
          <button type="button" id="cancel-save" data-cy="entityCreateCancelButton" class="btn btn-secondary" v-on:click="previousState()">
            <font-awesome-icon icon="ban"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.cancel')"></span>
          </button>
          <button
            type="submit"
            id="save-entity"
            data-cy="entityCreateSaveButton"
            :disabled="v$.$invalid || isSaving"
            class="btn btn-primary"
          >
            <font-awesome-icon icon="save"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.save')"></span>
          </button>
        </div>
      </form>
    </div>
  </div>
</template>
<script lang="ts" src="./trx-tournament-update.component.ts"></script>
