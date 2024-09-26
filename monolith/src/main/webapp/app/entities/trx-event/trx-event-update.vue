<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" novalidate v-on:submit.prevent="save()">
        <h2
          id="monolithApp.trxEvent.home.createOrEditLabel"
          data-cy="TrxEventCreateUpdateHeading"
          v-text="t$('monolithApp.trxEvent.home.createOrEditLabel')"
        ></h2>
        <div>
          <div class="form-group" v-if="trxEvent.id">
            <label for="id" v-text="t$('global.field.id')"></label>
            <input type="text" class="form-control" id="id" name="id" v-model="trxEvent.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('monolithApp.trxEvent.title')" for="trx-event-title"></label>
            <input
              type="text"
              class="form-control"
              name="title"
              id="trx-event-title"
              data-cy="title"
              :class="{ valid: !v$.title.$invalid, invalid: v$.title.$invalid }"
              v-model="v$.title.$model"
              required
            />
            <div v-if="v$.title.$anyDirty && v$.title.$invalid">
              <small class="form-text text-danger" v-for="error of v$.title.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('monolithApp.trxEvent.description')" for="trx-event-description"></label>
            <textarea
              class="form-control"
              name="description"
              id="trx-event-description"
              data-cy="description"
              :class="{ valid: !v$.description.$invalid, invalid: v$.description.$invalid }"
              v-model="v$.description.$model"
            ></textarea>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('monolithApp.trxEvent.date')" for="trx-event-date"></label>
            <div class="d-flex">
              <input
                id="trx-event-date"
                data-cy="date"
                type="datetime-local"
                class="form-control"
                name="date"
                :class="{ valid: !v$.date.$invalid, invalid: v$.date.$invalid }"
                required
                :value="convertDateTimeFromServer(v$.date.$model)"
                @change="updateInstantField('date', $event)"
              />
            </div>
            <div v-if="v$.date.$anyDirty && v$.date.$invalid">
              <small class="form-text text-danger" v-for="error of v$.date.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('monolithApp.trxEvent.location')" for="trx-event-location"></label>
            <input
              type="text"
              class="form-control"
              name="location"
              id="trx-event-location"
              data-cy="location"
              :class="{ valid: !v$.location.$invalid, invalid: v$.location.$invalid }"
              v-model="v$.location.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('monolithApp.trxEvent.capacity')" for="trx-event-capacity"></label>
            <input
              type="number"
              class="form-control"
              name="capacity"
              id="trx-event-capacity"
              data-cy="capacity"
              :class="{ valid: !v$.capacity.$invalid, invalid: v$.capacity.$invalid }"
              v-model.number="v$.capacity.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('monolithApp.trxEvent.price')" for="trx-event-price"></label>
            <input
              type="number"
              class="form-control"
              name="price"
              id="trx-event-price"
              data-cy="price"
              :class="{ valid: !v$.price.$invalid, invalid: v$.price.$invalid }"
              v-model.number="v$.price.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('monolithApp.trxEvent.status')" for="trx-event-status"></label>
            <select
              class="form-control"
              name="status"
              :class="{ valid: !v$.status.$invalid, invalid: v$.status.$invalid }"
              v-model="v$.status.$model"
              id="trx-event-status"
              data-cy="status"
            >
              <option
                v-for="eventStatus in eventStatusValues"
                :key="eventStatus"
                v-bind:value="eventStatus"
                v-bind:label="t$('monolithApp.EventStatus.' + eventStatus)"
              >
                {{ eventStatus }}
              </option>
            </select>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('monolithApp.trxEvent.service')" for="trx-event-service"></label>
            <select class="form-control" id="trx-event-service" data-cy="service" name="service" v-model="trxEvent.service">
              <option v-bind:value="null"></option>
              <option
                v-bind:value="trxEvent.service && mstServiceOption.id === trxEvent.service.id ? trxEvent.service : mstServiceOption"
                v-for="mstServiceOption in mstServices"
                :key="mstServiceOption.id"
              >
                {{ mstServiceOption.id }}
              </option>
            </select>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('monolithApp.trxEvent.testimonial')" for="trx-event-testimonial"></label>
            <select class="form-control" id="trx-event-testimonial" data-cy="testimonial" name="testimonial" v-model="trxEvent.testimonial">
              <option v-bind:value="null"></option>
              <option
                v-bind:value="
                  trxEvent.testimonial && trxTestimonialOption.id === trxEvent.testimonial.id ? trxEvent.testimonial : trxTestimonialOption
                "
                v-for="trxTestimonialOption in trxTestimonials"
                :key="trxTestimonialOption.id"
              >
                {{ trxTestimonialOption.id }}
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
<script lang="ts" src="./trx-event-update.component.ts"></script>
